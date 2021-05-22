

-- osm2pgsql -c -s mo-spe-all.osm.pbf -r pbf -O flex --style=config_for_places.lua --database=gis --user=gis_admin --password --host=localhost --port=5434 --prefix=eco

-- inspect = require('inspect')

local eco_places = osm2pgsql.define_table {
    name = "eco_places",
    ids = { type = 'any', id_column = 'osm_id', type_column = 'osm_type' },

    columns = {
        { column = 'id', type = 'serial', create_only = true },
        { column = 'type', type = 'text', not_null = true },
        { column = 'geom', type = 'geometry', projection = 4326},
        { column = 'name', type = 'text' },
        { column = 'description', type = 'text' },
        { column = 'processed', type = 'bool', not_null = true },
        { column = 'attrs', type = 'hstore' },
        { column = 'tags',  type = 'hstore' },
    }
}

local ways = osm2pgsql.define_way_table('ways', {
    { column = 'name',     type = 'text' },
    { column = 'type',     type = 'text', not_null = true },
    { column = 'id',       type = 'serial', create_only = true },
    { column = 'oneway',   type = 'direction' },
    { column = 'tags',     type = 'hstore' },
    { column = 'nodes',    type = 'int8[]' },
    { column = 'geom',     type = 'linestring', projection = 4326},
    { column = 'priority',     type = 'real' },
    { column = 'maxspeed',     type = 'int' },
})

-- print("columns=" .. inspect(eco_places:columns()))

local highway_types_m = {
    'motorway',
    'trunk',
    'primary',
    'secondary',
}

local places_types_leisure_m = {
    'park', 'nature_reserve'
}

local places_types_amenity_m = {
    'fuel'
}

local places_types_landuse_m = {
    'industrial'
}

local highway_types = {}
local places_types_leisure = {}
local places_types_amenity = {}
local places_types_landuse = {}

for _, k in ipairs(highway_types_m) do
    highway_types[k] = 1
end

for _, k in ipairs(places_types_leisure_m) do
    places_types_leisure[k] = 1
end

for _, k in ipairs(places_types_amenity_m) do
    places_types_amenity[k] = 1
end

for _, k in ipairs(places_types_landuse_m) do
    places_types_landuse[k] = 1
end

function parse_speed(input)
    if not input then
        return nil
    end

    local maxspeed = tonumber(input)

    -- If maxspeed is just a number, it is in km/h, so just return it
    if maxspeed then
        return maxspeed
    end

    -- If there is an 'mph' at the end, convert to km/h and return
    if input:sub(-3) == 'mph' then
        local num = tonumber(input:sub(1, -4))
        if num then
            return math.floor(num * 1.60934)
        end
    end

    return nil
end

function clean_tags(tags)
    tags.odbl = nil
    tags.created_by = nil
    tags.source = nil
    tags['source:ref'] = nil

    return next(tags) == nil
end

function add_row(object, geometry_type, type) 
    eco_places:add_row({
        attrs = {
            version = object.version,
            timestamp = object.timestamp,
        },
        tags = object.tags,
        geom = { create = geometry_type },
        processed = false,
    
        name = object.tags.name,
        description = object.tags.description,
        type = type        
    })
end

function process(object, geometry_type, type) 
    if clean_tags(object.tags) then
        return
    end
    
    add_row(object,geometry_type,type)
end

function osm2pgsql.process_node(object)

    local type = object:grab_tag('amenity')

    if places_types_amenity[type] then
        process(object, 'point', type)
        return
    end
    
end

function osm2pgsql.process_way(object)
    local type = object:grab_tag('highway')
    
    if highway_types[type] then
        process(object, 'line', type)
    end

    if clean_tags(object.tags) then
        return
    end

    local name = object:grab_tag('name')

    if type == nil then
        return
    end

    ways:add_row({
        name = name,
        type = type,
        maxspeed = parse_speed(object.tags.maxspeed),
        oneway = object.tags.oneway or 0,
        nodes = '{' .. table.concat(object.nodes, ',') .. '}',
        tags = object.tags,
        priority = 404.0
    })
end


function add_area(object, type)
    if object.tags.type == 'multipolygon' or object.tags.type == 'boundary' then
        add_row(object, 'area', type)
    end
end

function osm2pgsql.process_relation(object)
    if clean_tags(object.tags) then
        return
    end

    local type = object:grab_tag('leisure')

    if places_types_leisure[type] then
        add_area(object, type)
        return
    end

    local type2 = object:grab_tag('landuse')

    if places_types_landuse[type2] then
        add_area(object, type2)
        return
    end
end

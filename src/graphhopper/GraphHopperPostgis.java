/*
 *  Licensed to GraphHopper GmbH under one or more contributor
 *  license agreements. See the NOTICE file distributed with this work for
 *  additional information regarding copyright ownership.
 *
 *  GraphHopper GmbH licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except in
 *  compliance with the License. You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package graphhopper;

import com.graphhopper.GraphHopper;
import com.graphhopper.GraphHopperConfig;
import com.graphhopper.reader.DataReader;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.storage.GraphHopperStorage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Modified version of GraphHopper to optimize working with Postgis
 *
 * @author Phil
 * @author Robin Boldt
 */
public class GraphHopperPostgis extends GraphHopperOSM {

    private final HashSet<OSMPostgisReader.EdgeAddedListener> edgeAddedListeners = new HashSet<>();
    private final Map<String, String> postgisParams = new HashMap<>();

    @Override
    public GraphHopper init(GraphHopperConfig ghConfig) {

        postgisParams.put("dbtype", "postgis");
        postgisParams.put("host", "localhost");
        postgisParams.put("port", "5434");
        postgisParams.put("schema", "public");
        postgisParams.put("database", "gis");
        postgisParams.put("user", "gis_admin");
        postgisParams.put("passwd", "admin");
        postgisParams.put("tags_to_copy", "priority");

        return super.init(ghConfig);
    }

    @Override
    protected DataReader createReader(GraphHopperStorage ghStorage) {
        OSMPostgisReader reader = new OSMPostgisReader(ghStorage, postgisParams);
        for (OSMPostgisReader.EdgeAddedListener l : edgeAddedListeners) {
            reader.addListener(l);
        }
        return initDataReader(reader);
    }

    // TODO do we need the EdgeAddedListener?
    public void addListener(OSMPostgisReader.EdgeAddedListener l) {
        edgeAddedListeners.add(l);
    }

}

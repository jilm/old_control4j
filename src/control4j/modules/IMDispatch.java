/*
 * Copyright (C) 2016 jilm
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package control4j.modules;

import control4j.AVariableInput;
import control4j.InputModule;
import control4j.ResourceManager;
import control4j.Signal;
import control4j.application.Module;
import control4j.protocols.tcp.SocketKey;
import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.dispatch.Client;
import cz.lidinsky.tools.reflect.Setter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONObject;

/**
 *
 * @author jilm
 */
@AVariableInput
public class IMDispatch extends InputModule {

    private String[] labels;

    private SocketKey key = new SocketKey("localhost", 12345);

    @Setter("host")
    public void setHost(String host) {
        key = key.setHost(host);
    }

    @Setter("port")
    public void setPort(int port) {
        key = key.setPort(port);
    }

    @Override
    public void initialize(Module definition) {
        super.initialize(definition);
        labels = new String[definition.getInput().size()];
        labels = (String[]) definition.getInput().stream()
                .map(input -> input.getSignal().getLabel())
                .toArray(size -> new String[size]);
    }

    @Override
    protected void put(Signal[] input, int inputLength) {
        try {
            // get the dispatcher object
            Client client = ResourceManager.getOrCreate(Client.class, key, this::connect);
            // transform signals into the JSON
            for (int i = 0; i < inputLength; i++) {
                client.send(getJSON(labels[i], input[i]));
            }
            // send them
        } catch (IOException ex) {
            Logger.getLogger(getClass().getName()).log(Level.SEVERE, null, ex);

        }
    }

    private JSONObject getJSON(String label, Signal signal) {
        JSONObject json = new JSONObject();
        json.put("timestamp", signal.getTimestamp().getTime());
        json.put("class", "control4j.Signal");
        json.put("label", label);
        json.put("isValid", signal.isValid());
        if (signal.isValid()) {
            json.put("value", signal.getValue());
        }
        return json;
    }

    private Client connect() {
        try {
            return new Client(key.getHost(), key.getPort());
        } catch (IOException ex) {
            throw new CommonException()
                    .setCause(ex);
        }
    }
}

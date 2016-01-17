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

import control4j.AVariableOutput;
import control4j.OutputModule;
import control4j.ResourceManager;
import control4j.RuntimeModuleException;
import control4j.Signal;
import control4j.application.Module;
import control4j.protocols.tcp.SocketKey;
import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.dispatch.Client;
import cz.lidinsky.tools.reflect.Setter;
import java.io.IOException;
import java.util.Arrays;
import org.json.JSONObject;

/**
 *
 * @author jilm
 */
@AVariableOutput
public class OMDispatch extends OutputModule {

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
        labels = definition.getOutput()
                .stream()
                .map(output -> output.getSignal().getLabel())
                .toArray(size -> new String[size]);
        received = new Signal[definition.getOutput().size()];
    }

    @Override
    protected void get(Signal[] output, int outputLength) throws RuntimeModuleException {
        System.arraycopy(received, 0, output, 0, outputLength);
        Arrays.fill(received, null);
    }

    private Signal[] received;

    @Override
    public void prepare() {
        Client client = ResourceManager.getOrCreate(Client.class, key, this::connect);
        client.addConsumer(this::receive);
    }

    private Client connect() {
        try {
            Client client = new Client(key.getHost(), key.getPort());
            client.start();
            return client;

        } catch (IOException ex) {
            throw new CommonException()
                    .setCause(ex);
        }
    }


    private void receive(Object object) {
        if (object instanceof JSONObject) {
            JSONObject json = (JSONObject)object;
            if (json.get("class").equals("control4j.Signal")) {
                String label = (String)json.get("label");
                int index = Arrays.asList(labels).indexOf(label);
                if (index >= 0) {
                    long timestamp = json.getLong("timestamp");
                    boolean isValid = json.getBoolean("isValid");
                    if (isValid) {
                        double value = json.getDouble("value");
                        received[index] = Signal.getSignal(value);
                    } else {
                        received[index] = Signal.getSignal();
                    }
                }
            }
        }
    }

}

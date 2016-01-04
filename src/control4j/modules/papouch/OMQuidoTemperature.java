/*
 *  Copyright 2016 Jiri Lidinsky
 *
 *  This file is part of control4j.
 *
 *  control4j is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, version 3.
 *
 *  control4j is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with control4j.  If not, see <http://www.gnu.org/licenses/>.
 */

package control4j.modules.papouch;

import control4j.AOutputSize;
import control4j.ICycleEventListener;
import control4j.Signal;
import control4j.protocols.spinel.SpinelException;
import control4j.protocols.spinel.SpinelMessage;

/**
 *
 *
 */
@AOutputSize(1)
public class OMQuidoTemperature extends Papouch implements ICycleEventListener {

    /**
     * Creates a request for new measurement.
     */
    @Override
    public void prepare() {
        request
            = new SpinelMessage(
                   address, control4j.hw.papouch.Quido.TEMPERATURE_MEASUREMENT, new int[] {1});
    }
    
    @Override
    protected void get(Signal[] output, int outputLength) {
        if (status == 0) {
            try {
                double temperature = control4j.hw.papouch.Quido.getOneTimeTemperatureMeasurement(responseMessage);
                output[0] = Signal.getSignal(temperature, timestamp);
            } catch (SpinelException spinelException) {
                status = 1;
            }
        }
    }

}
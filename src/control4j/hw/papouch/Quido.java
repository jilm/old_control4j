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

package control4j.hw.papouch;

import java.io.IOException;
import control4j.tools.IResponseCrate;
import control4j.protocols.spinel.SpinelOverTcp;
import control4j.protocols.spinel.SpinelMessage;
import control4j.protocols.spinel.SpinelException;
import control4j.scanner.Getter;

/**
 *
 */
public class Quido extends Papouch {

    public static final int TEMPERATURE_MEASUREMENT = 0x51;
    public static final int READ_BINARY_INPUT = 0x31;
    public static final int SET_OUTPUT = 0x20;

    public Quido(SpinelOverTcp channel, int address) {
        super(channel, address);
    }
    
    public static double getOneTimeTemperatureMeasurement(SpinelMessage message)
            throws SpinelException {
        if (message.getInst() == 0) {
            int rawValue = (int) ((message.getData(1) << 8) + message.getData(2));
            double value = (double) rawValue * 0.1d;
            return value;
        } else {
            throw new SpinelException();
        }
    }
    
    public static int getBinaryInput(SpinelMessage message)
            throws SpinelException {
        if (message.getInst() == 0) {
            return message.getData(0);
        } else {
            throw new SpinelException();
        }
    }
    
    public static SpinelMessage getSetOutputMessage(int address, int output, int mask) {
        int[] data = new int[8];
        int count = 0;
        int j = 1;
        for (int i = 0; i < 8; i++) {
            if ((mask & j) > 0) {
                data[count++] = (output & j) / j * 0x80 + i;
            }
            j *= 2;
        }
        int[] result = new int[count];
        System.arraycopy(data, 0, result, 0, count);
        return new SpinelMessage(address, SET_OUTPUT, result);
    }

}
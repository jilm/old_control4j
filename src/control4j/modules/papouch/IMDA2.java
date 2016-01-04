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

import control4j.AMaxInput;
import control4j.AOutputSize;
import java.io.IOException;
import java.util.Date;
import control4j.ICycleEventListener;
import control4j.InputModule;
import control4j.OutputModule;
import control4j.ResourceManager;
import control4j.Signal;
import control4j.application.Module;
import control4j.protocols.spinel.SpinelMessage;
import control4j.protocols.spinel.SpinelOverTcp;
import control4j.protocols.tcp.SocketKey;
import control4j.tools.IResponseCrate;
import static control4j.tools.Logger.*;
import control4j.tools.Tools;
import cz.lidinsky.tools.CommonException;
import cz.lidinsky.tools.reflect.Setter;
import java.util.Objects;

/**
 */
@AMaxInput(2)
public class IMDA2 extends IMPapouch {

    @Override
    public void initialize(Module definition) {
        super.initialize(definition); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    @Override
    protected void put(Signal[] input, int inputLength) {
        if (inputLength > 0 && input[0] != null && input[0].isValid()) {
            request = createMessage(address, 0, input[0].getValue());
        } else {
            request = null;
        }
    }
    
    protected static SpinelMessage createMessage(int address, int channel, double value) {
        int raw = (int)(value * 4095);
        int msb = (raw & 0xf00) / 0x100;
        int lsb = raw & 0xff;
        int[] data = new int[] {channel + 1, msb, lsb};
        SpinelMessage result = new SpinelMessage(address, 0x40, data);
        return result;
    }

}
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
@AMaxInput(8)
public class IMQuidoBinary extends IMPapouch {

    private int[] data = new int[8];

    @Override
    public void initialize(Module definition) {
        super.initialize(definition); //To change body of generated methods, choose Tools | Templates.
    }

    
    
    @Override
    protected void put(Signal[] input, int inputLength) {
        int counter = 0;
        for (int i = 0; i < inputLength; i++) {
            if (input[i] != null && input[i].isValid()) {
                data[counter++] = input[i].getBoolean() ? 0x80 + i : i;
            }
        }
        request = new SpinelMessage(address, control4j.hw.papouch.Quido.SET_OUTPUT, data, 0, counter);
    }

}
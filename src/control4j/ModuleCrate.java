package control4j;

/*
 *  Copyright 2015 Jiri Lidinsky
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

abstract class ModuleCrate {

  static ModuleCrate create(
      Module module, int[] inputMap, int[] outputMap) {

    if (module instanceof InputModule) {
      return new InputModuleCrate((InputModule)module, inputMap);
    } else if (module instanceof OutputModule) {
      return new OutputModuleCrate((OutputModule)module, outputMap);
    } else if (module instanceof ProcessModule) {
      return new ProcessModuleCrate((ProcessModule)module, inputMap, outputMap);
    } else {
      throw new SyntaxErrorException()
        .setCode(ExceptionCode.CLASS_CAST)
        .set("message", "Module implementation")
        .set("module class", module.getClass().getName());
    }
  }

  abstract void prepare();

  abstract void execute(DataBuffer buffer);

  abstract int getMaxSignalPointer();

  private static Signal[] inputArray;

  protected static Signal[] getInputArray(int size) {
    if (inputArray == null)
      inputArray = new Signal[size];
    else if (inputArray.length < size)
      inputArray = new Signal[size];
    return inputArray;
  }

  private static Signal[] outputArray;

  protected static Signal[] getOutputArray(int size) {
    if (outputArray == null)
      outputArray = new Signal[size];
    else if (outputArray.length < size)
      outputArray = new Signal[size];
    return outputArray;
  }

  protected static int max(int[] map) {
    if (map == null) {
      return 0;
    }
    int max = 0;
    for (int i : map) {
      max = Math.max(max, i);
    }
    return max;
  }

  private static class InputModuleCrate extends ModuleCrate {

    private InputModule module;
    private int[] inputMap;

    InputModuleCrate(InputModule module, int[] inputMap) {
      this.module = module;
      if (inputMap != null) {
        this.inputMap = inputMap;
      } else if (module.getMandatoryInputSize() > 0) {
        throw new SyntaxErrorException()
          .setCode(ExceptionCode.NULL_POINTER)
          .set("message", "Input module without input!")
          .set("input required", module.getMandatoryInputSize())
          .set("input found", "0")
          .set("reference", module.getDeclarationReference());
      } else {
        this.inputMap = new int[0];
      }
    }

    @Override
    void execute(DataBuffer buffer) {
      Signal[] input = buffer.get(inputMap);
      module.put(input, inputMap.length);
    }

    @Override
    void prepare() {
      module.prepare();
    }

    @Override
    int getMaxSignalPointer() {
      return max(inputMap);
    }

  }

  private static class OutputModuleCrate extends ModuleCrate {

    private OutputModule module;
    private int[] outputMap;

    OutputModuleCrate(OutputModule module, int[] outputMap) {
      this.module = module;
      if (outputMap != null) {
        this.outputMap = outputMap;
      } else {
        this.outputMap = new int[0];
      }
    }

    @Override
    void execute(DataBuffer buffer) {
      Signal[] output = getOutputArray(outputMap.length);
      module.get(output, outputMap.length);
      buffer.put(output, outputMap);
    }

    @Override
    void prepare() {
      module.prepare();
    }

    @Override
    int getMaxSignalPointer() {
      return max(outputMap);
    }

  }

  private static class ProcessModuleCrate extends ModuleCrate {

    private ProcessModule module;
    private int[] inputMap;
    private int[] outputMap;

    ProcessModuleCrate(ProcessModule module, int[] inputMap, int[] outputMap) {
      this.module = module;
      if (inputMap != null) {
        this.inputMap = inputMap;
      } else if (module.getMandatoryInputSize() > 0) {
        throw new SyntaxErrorException()
          .setCode(ExceptionCode.NULL_POINTER)
          .set("message", "Process module without input!")
          .set("input required", module.getMandatoryInputSize())
          .set("input found", "0")
          .set("reference", module.getDeclarationReference());
      } else {
        this.inputMap = new int[0];
      }
      if (outputMap != null) {
        this.outputMap = outputMap;
      } else {
        this.outputMap = new int[0];
      }
    }

    @Override
    void execute(DataBuffer buffer) {
      Signal[] input = buffer.get(inputMap);
      Signal[] output = getOutputArray(outputMap.length);
      module.process(input, inputMap.length, output, outputMap.length);
      buffer.put(output, outputMap);
    }

    @Override
    void prepare() {
      module.prepare();
    }

    @Override
    int getMaxSignalPointer() {
      return Math.max(max(inputMap), max(outputMap));
    }

  }

}

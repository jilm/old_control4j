<ld>

  <rung>
    <comment>The door is open</comment>
    <contact name="switch1" />
    <contact name="switch2" />
    <coil name="door-motor" />
  </rung>

  <rung>
    <contact name="close-door" />
    <contact name="obstruction" not="true" />
    <coil name="door-motor" />
  </rung>

  <rung>
    <parallel>
      <contact name="exterior-unlock" />
      <contact name="interior-unlock" />
    </parallel>
    <coil name="unlock" />
  </rung>

  <rung>
    <parallel>
      <contact name="start" />
      <contact name="run" />
    </parallel>
    <contact name="stop" not="true" />
    <parallel>
      <coil name="run" />
      <coil name="set" type="s" />
    </parallel>
  </rung>

  <rung>
    <parallel>
      <serial>
        <contact name="a" />
	<contact name="b" />
      </serial>
      <contact name="c" />
    </parallel>
    <coil name="d" type="n" />
  </rung>

</ld>

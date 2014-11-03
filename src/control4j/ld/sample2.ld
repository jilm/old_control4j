<ld>

  <rung>
    <contact name="tl1" />
    <coil name="led1" />
  </rung>

  <rung>
    <serial>
      <contact name="start" />
      <contact name="stop" />
    </serial>
    <coil name="motor" />
  </rung>

  <rung>
    <parallel>
      <serial>
        <contact name="tl2" />
	<contact name="tl3" />
      </serial>
      <contact name="tl4" />
      <serial>
        <contact name="tl5" />
      </serial>
    </parallel>
    <coil name="led6" />
    <coil name="led7" />
  </rung>

</ld>

# Audio Device Selector

This module includes two tools:
- `AudioDeviceSelector` to make easier to choose between different Android audio outputs
- `VeraAudioDevice` is a Vonage `AudioDevice` class to override default behavior in Vonage Video SDK

`VeraAudioDevice` is needed to allow `AudioDeviceSelector` to switch between different audio outputs.

## How it works
Supports 4 types of device output
- Bluetooth headset devices
- Wired headset devices
- Earpiece
- Speaker

## Expected behavior
Note: Currently the audio device priority queue is fixed as: `BLUETOOTH -> WIRED_HEADSET -> SPEAKER -> EARPIECE`

- Bluetooth enabled -> enter to meeting room
  - Audio goes to Bluetooth
  - When disable Bluetooth -> fallback to wired headset or earpiece (depends on priority queue)
- Bluetooth disabled -> enter to meeting room
  - Audios goes to next audio device in priority queue
  - When enable Bluetooth -> reset current audio device and route to bluetooth

## Pending
- [ ] Get bluetooth device name
- [ ] Make device priority queue configurable
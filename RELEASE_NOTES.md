# wellen / release notes

## v0.5 (20201208)

- renamed project to *wellen*
- prepared release on processing.org website
- added `BeatDSP` to generate a beat events from `DSP`
- added `ExampleDSP10SampleRecorder` which demonstrates how to record data and play it back with `Sampler`
- enabled `Sampler` to play samples backwards
- added `ExampleInstruments08CustomDSPInstrument` which demonstrates how to implement a custom instrument with multiple oscillators and a kick drum
- added `FFT` for analyzing audio signals
- released a series of screencasts on youtube explaining some of the examples: [Wellen](https://www.youtube.com/playlist?list=PLXJNr6N-Bu4NzkP4UJ5m-9721MdaZ6v-q)
- cleaned up method names to comply to *methods start with verb* paradigm

## v0.4 (20201202)

- implemented *software* tone engine ( based on DSP classes )
- removed dependency on external sound libraries ( minim + jsyn )
- removed GUI examples with controlP5
- added panning to instrument
- added LFOs to instrument
- added LPF to instrument
- made instrument options ( ADSR+LPF+LFOs ) more coherent
- *software* tone engine now allows to post process output with DSP
- added Low-Pass Filter class ( Moog ladder )
- improved `Beat` class to accept any object for callback
- improved `ADSR` ( removed clicks when triggered at high frequency )
- added interpolation between sample in `Wavetable` ( helps with small buffer LFOs )
- added `Trigger` to allow triggering on rising and falling edges
- introduced interfaces for input, output and process for DSP classes ( more semantic than functional )
- renamed package

## v0.3 (20201124)

- cleaned up and rearranged
- added sampler
- added algorithmic composition applications
- fixed `note_on` with scheduled `note_off` events

## v0.2 (20201109)

- cleaned up examples
- added wavetable + arpeggiator
- added *song position* + BPM estimate in MIDI clock

## v0.1 (20201104)

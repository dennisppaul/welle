import wellen.*; 
import wellen.dsp.*; 

/*
 * this example demonstrates how to write data directly into the audio buffer sample by sample. the `audio()` method
 * is called and is expected to return a single sample in the value range [-1.0, 1.0].
 *
 * by moving the mouse up and down the perceived volume of the output is manipulated.
 *
 * note that although this example is very simple and easy to understand it is adivsed to process audio in blocks as
 * demonstrated in the example `ExampleDSP00Audioblock`.
 */
void settings() {
    size(640, 480);
}
void setup() {
    Wellen.dumpAudioInputAndOutputDevices(true);
    DSP.start(this);
}
void draw() {
    background(255);
    DSP.draw_buffers(g, width, height);
}
float audio() {
    float output = random(-0.1f, 0.1f);
    float amplification = map(mouseY, 0, height, 0.0f, 1.0f);
    return output * amplification;
}

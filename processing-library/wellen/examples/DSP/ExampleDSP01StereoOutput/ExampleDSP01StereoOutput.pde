import wellen.*; 
import wellen.dsp.*; 

/*
 * this example demonstrates how to create stereo sounds with DSP. two slightly detuned sine waves are generated and
 * distributed to the left and right channel.
 *
 * note that the distortion in the signal stems from changing the frequency too abruptly.
 */
int mCounter = 0;
float mDetune = 1.1f;
float mFreq = 344.53125f;
void settings() {
    size(640, 480);
}
void setup() {
    Wellen.dumpAudioInputAndOutputDevices();
    DSP.start(this, 2);
}
void draw() {
    background(255);
    stroke(0);
    DSP.draw_buffers(g, width, height);
}
void mouseMoved() {
    mFreq = map(mouseX, 0, width, 86.1328125f, 344.53125f);
    mDetune = map(mouseY, 0, height, 1.0f, 1.5f);
}
void audioblock(float[] output_signalLeft, float[] output_signalRight) {
    for (int i = 0; i < output_signalLeft.length; i++) {
        mCounter++;
        float mLeft = 0.5f * sin(2 * PI * mFreq * mCounter / DSP.get_sample_rate());
        float mRight = 0.5f * sin(2 * PI * mFreq * mDetune * mCounter / DSP.get_sample_rate());
        output_signalLeft[i] = mLeft * 0.7f + mRight * 0.3f;
        output_signalRight[i] = mLeft * 0.3f + mRight * 0.7f;
    }
}

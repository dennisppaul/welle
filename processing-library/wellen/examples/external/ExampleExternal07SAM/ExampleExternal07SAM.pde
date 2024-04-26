import wellen.*; 
import wellen.dsp.*; 

/*
 * this example demonstrates how to use the SAM ( Software Automatic Mouth ) a speech software first published in
 * 1982 for Commodore C64 ( macOS + linux only ).
 *
 * move and drag mouse to change parameters.
 */
SAM mSAM;
String[] mWords;
void settings() {
    size(640, 480);
}
void setup() {
    mSAM = new SAM();
    String mText = "I know not by what power I am made bold, Nor how it may concern my modesty, In such a " +
            "presence here to plead my thoughts; But I beseech your grace that I may know The worst that may " +
            "befall me in this case, If I refuse to wed Demetrius.";
    mWords = split(mText, ' ');
    DSP.start(this);
    Beat.start(this, 140);
}
void mouseMoved() {
    mSAM.set_pitch((int) map(mouseX, 0, width, 0, 255));
    mSAM.set_throat((int) map(mouseY, 0, height, 0, 255));
}
void mouseDragged() {
    mSAM.set_speed((int) map(mouseX, 0, width, 0, 255));
    mSAM.set_mouth((int) map(mouseY, 0, height, 0, 255));
}
void draw() {
    background(255);
    stroke(0);
    DSP.draw_buffers(g, width, height);
}
void beat(int beatCount) {
    int mWordIndex = beatCount % mWords.length;
    mSAM.say(mWords[mWordIndex]);
}
void audioblock(float[] output_signal) {
    for (int i = 0; i < output_signal.length; i++) {
        output_signal[i] = mSAM.output() * 0.5f;
    }
}

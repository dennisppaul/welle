import wellen.*; 
import wellen.dsp.*; 

/*
 * this example demonstrates how to use the built-in low-frequency oscillators (LFOs) to change the sound
 * characteristics of an instrument.
 *
 * use keys `1` and `2` to enable/disable LFOs and ` ` to toggle parameter selection.
 *
 * note that this functionality is not implemented for MIDI and OSC.
 */
boolean mEnableAmplitudeLFO       = false;
boolean mEnableFrequencyLFO       = true;
boolean mToggleLFOParameterSelect = true;
void settings() {
    size(640, 480);
}
void setup() {
    Tone.start();
    Tone.instrument().set_frequency_LFO_amplitude(2.0f);
    Tone.instrument().set_frequency_LFO_frequency(11.0f);
    Tone.instrument().enable_frequency_LFO(true);
}
void draw() {
    background(255);
    noStroke();
    fill(0);
    ellipse(width * 0.5f, height * 0.5f, Tone.is_playing() ? 100 : 5, Tone.is_playing() ? 100 : 5);
    ellipse(40, height * 0.5f, mEnableFrequencyLFO ? 20 : 5, mEnableFrequencyLFO ? 20 : 5);
    ellipse(80, height * 0.5f, mEnableAmplitudeLFO ? 20 : 5, mEnableAmplitudeLFO ? 20 : 5);
    stroke(0);
    noFill();
    ellipse(mToggleLFOParameterSelect ? 40 : 80, height * 0.5f, 25, 25);
}
void mousePressed() {
    int mNote = 45 + (int) random(0, 12);
    Tone.note_on(mNote, 100);
}
void mouseReleased() {
    Tone.note_off();
}
void mouseDragged() {
    if (mToggleLFOParameterSelect) {
        Tone.instrument().set_frequency_LFO_amplitude(map(mouseY, 0, height, 0.0f, 50.0f));
        Tone.instrument().set_frequency_LFO_frequency(map(mouseX, 0, width, 0.0f, 50.0f));
        println("LFO_amplitude: " + Tone.instrument().get_frequency_LFO_amplitude());
        println("LFO_frequency: " + Tone.instrument().get_frequency_LFO_frequency());
    } else {
        Tone.instrument().set_amplitude_LFO_amplitude(map(mouseY, 0, height, 0.0f, 1.0f));
        Tone.instrument().set_amplitude_LFO_frequency(map(mouseX, 0, width, 0.0f, 50.0f));
        println("LFO_amplitude: " + Tone.instrument().get_amplitude_LFO_amplitude());
        println("LFO_frequency: " + Tone.instrument().get_amplitude_LFO_frequency());
    }
}
void keyPressed() {
    switch (key) {
        case '1':
            mEnableFrequencyLFO = !mEnableFrequencyLFO;
            Tone.instrument().enable_frequency_LFO(mEnableFrequencyLFO);
            break;
        case '2':
            mEnableAmplitudeLFO = !mEnableAmplitudeLFO;
            Tone.instrument().enable_amplitude_LFO(mEnableAmplitudeLFO);
            break;
        case ' ':
            mToggleLFOParameterSelect = !mToggleLFOParameterSelect;
            break;
    }
}

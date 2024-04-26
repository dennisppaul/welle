import wellen.*; 
import wellen.dsp.*; 

/*
 * this example shows how to use different oscillators in an instrument.
 *
 * it also illustrates how to set the amplitude of an oscillator directly with the `set_amplitude(float, int)`
 * method. note that this method requires two parameters of which the second one defines the interpolation speed
 * between old and new amplitude. this technique should be used when changing the amplitude at a high rate to
 * prevent crackling artifacts.
 *
 * use keys `1` – `5` to select different wave shapes.
 *
 * note that these functionalities are not implemented for MIDI and OSC.
 */
int mNote;
void settings() {
    size(640, 480);
}
void setup() {
    background(255);
}
void draw() {
    background(255);
    fill(0);
    float mSize = map(mNote, 33, 69, 80, 320);
    ellipse(width * 0.5f, height * 0.5f, Tone.is_playing() ? mSize : 5, Tone.is_playing() ? mSize : 5);
}
void mousePressed() {
    mNote = Scale.get_note(Scale.CHORD_MAJOR_7TH, Note.NOTE_A2, (int) random(0, 10));
    Tone.note_on(mNote, 100);
}
void mouseDragged() {
    float mAmplitude = map(mouseY, 0, height, 0.0f, 1.0f);
    int mInterpolationSpeedInSamples = Wellen.millis_to_samples(100);
    Tone.instrument().set_amplitude(mAmplitude, mInterpolationSpeedInSamples);
}
void mouseReleased() {
    Tone.note_off();
}
void keyPressed() {
    if (key == '1') {
        Tone.instrument().set_oscillator_type(Wellen.WAVEFORM_SINE);
    }
    if (key == '2') {
        Tone.instrument().set_oscillator_type(Wellen.WAVEFORM_TRIANGLE);
    }
    if (key == '3') {
        Tone.instrument().set_oscillator_type(Wellen.WAVEFORM_SAWTOOTH);
    }
    if (key == '4') {
        Tone.instrument().set_oscillator_type(Wellen.WAVEFORM_SQUARE);
    }
    if (key == '5') {
        Tone.instrument().set_oscillator_type(Wellen.WAVEFORM_NOISE);
    }
}

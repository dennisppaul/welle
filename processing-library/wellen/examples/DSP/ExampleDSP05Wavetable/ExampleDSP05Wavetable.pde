import wellen.*; 
import wellen.dsp.*; 

/*
 * this example demonstrates how to use a *wavetable* ( a chunk of memory ) and play it back at different
 * frequencies and amplitudes. in this example a wavetable is used to emulate an oscillator (VCO) with different
 * wave shapes. use keys `1`–`8` to select waveshapes.
 *
 * this example also demonstrates the effect of different interpolation methods for sample data. use keys
 * `+` and `-` to change the table size of a wave shape. use keys `q`, `w` and `e` to select an interpolation
 * method. note that the effect of interpolation can be quite significant at small table sizes.
 *
 * and lastly this example also demonstrates how to modify amplitude and frequency of an oscillator with
 * interpolation. use `SPACE` to toggle interpolation. see explantion inline.
 */

boolean fInterpolateAmplitudeAndFrequency = false;

Wavetable fWavetable;

int fWavetableSize = 6;

void settings() {
    size(640, 480);
}

void setup() {
    init_wavetable();
    DSP.start(this);
}

void draw() {
    background(255);
    if (fInterpolateAmplitudeAndFrequency) {
        fill(0);
        circle(20, 20, 32);
    }
    DSP.draw_buffers(g, width, height);
}

void mouseDragged() {
    fWavetable.set_frequency(2.0f * Wellen.DEFAULT_SAMPLING_RATE / Wellen.DEFAULT_AUDIOBLOCK_SIZE);
    fWavetable.set_amplitude(0.25f);
}

void mouseMoved() {
    final float mNewFrequency = map(mouseX, 0, width, 55, 880);
    final float mNewAmplitude = map(mouseY, 0, height, 0.0f, 0.9f);
    if (fInterpolateAmplitudeAndFrequency) {
        /* these versions of `set_frequency` + `set_amplitude` take a second paramter which define the duration in
         * samples from current to new value. on the one hands this prevents unwanted artifacts ( e.g crackling
         * noise when changing amplitude quickly especially on smoother wave shape like sine waves ) and on the
         * other hand can be used to create glissando or portamento effects.
         */
        fWavetable.set_frequency(mNewFrequency, Wellen.seconds_to_samples(0.5f));
        fWavetable.set_amplitude(mNewAmplitude, Wellen.millis_to_samples(10));
    } else {
        fWavetable.set_frequency(mNewFrequency);
        fWavetable.set_amplitude(mNewAmplitude);
    }
}

void keyPressed() {
    switch (key) {
        case '1':
            Wavetable.sine(fWavetable.get_wavetable());
            break;
        case '2':
            Wavetable.triangle(fWavetable.get_wavetable());
            break;
        case '3':
            Wavetable.sawtooth(fWavetable.get_wavetable());
            break;
        case '4':
            Wavetable.square(fWavetable.get_wavetable());
            break;
        case '5':
            Wavetable.triangle(fWavetable.get_wavetable(), 2);
            break;
        case '6':
            Wavetable.sawtooth(fWavetable.get_wavetable(), 8);
            break;
        case '7':
            Wavetable.square(fWavetable.get_wavetable(), 16);
            break;
        case '8':
            /* note, that this method is supposed to illustrate how to write data directly into the wavetable
             * buffer. in this case the same effect can be achieved by calling:
             * `Wavetable.noise(mWavetable.get_wavetable());`
             */
            randomize(fWavetable.get_wavetable());
            break;
        case '+':
            fWavetableSize++;
            init_wavetable();
            break;
        case '-':
            fWavetableSize--;
            if (fWavetableSize < 1) {
                fWavetableSize = 1;
            }
            init_wavetable();
            break;
        case 'q':
            fWavetable.set_interpolation(Wellen.WAVESHAPE_INTERPOLATE_NONE);
            break;
        case 'w':
            fWavetable.set_interpolation(Wellen.WAVESHAPE_INTERPOLATE_LINEAR);
            break;
        case 'e':
            fWavetable.set_interpolation(Wellen.WAVESHAPE_INTERPOLATE_CUBIC);
            break;
        case ' ':
            fInterpolateAmplitudeAndFrequency = !fInterpolateAmplitudeAndFrequency;
            break;
    }
}

void audioblock(float[] output_signal) {
    for (int i = 0; i < output_signal.length; i++) {
        output_signal[i] = fWavetable.output();
    }
}

void init_wavetable() {
    fWavetable = new Wavetable(1 << fWavetableSize);
    Wavetable.sine(fWavetable.get_wavetable());
}

void randomize(float[] pWavetable) {
    for (int i = 0; i < pWavetable.length; i++) {
        pWavetable[i] = random(-1, 1);
    }
}

import wellen.*; 
import wellen.dsp.*; 

import wellen.extra.rakarrack.*;
RRAnalogFilter mAnalogFilter;
final float mBaseFrequency = 4.0f * Wellen.DEFAULT_SAMPLING_RATE / Wellen.DEFAULT_AUDIOBLOCK_SIZE;
final float mMasterVolume = 0.75f;
final Wavetable mVCO1 = new Wavetable(512);
final Wavetable mVCO2 = new Wavetable(512);
void settings() {
    size(640, 480);
}
void setup() {
    Wavetable.square(mVCO1.get_wavetable());
    mVCO1.set_frequency(mBaseFrequency);
    mVCO1.set_amplitude(0.75f);
    Wavetable.square(mVCO2.get_wavetable());
    mVCO2.set_frequency(mBaseFrequency * 0.99f);
    mVCO2.set_amplitude(0.75f);
    mAnalogFilter = new RRAnalogFilter(RRAnalogFilter.TYPE_LPF_1_POLE, 30, 1, 0);
    DSP.start(this);
}
void draw() {
    background(255);
    DSP.draw_buffers(g, width, height);
}
void mouseMoved() {
    mAnalogFilter.setq(map(mouseY, 0, height, 0.0f, 4.0f));
    mAnalogFilter.setfreq(map(mouseX, 0, width, 0.0f, 10000.0f));
}
void keyPressed() {
    switch (key) {
        case 'q':
            Wavetable.fill(mVCO2.get_wavetable(), Wellen.WAVEFORM_SINE);
            break;
        case 'w':
            Wavetable.fill(mVCO2.get_wavetable(), Wellen.WAVEFORM_TRIANGLE);
            break;
        case 'e':
            Wavetable.fill(mVCO2.get_wavetable(), Wellen.WAVEFORM_SAWTOOTH);
            break;
        case 'r':
            Wavetable.fill(mVCO2.get_wavetable(), Wellen.WAVEFORM_SQUARE);
            break;
        case 'a':
            Wavetable.fill(mVCO1.get_wavetable(), Wellen.WAVEFORM_SINE);
            break;
        case 's':
            Wavetable.fill(mVCO1.get_wavetable(), Wellen.WAVEFORM_TRIANGLE);
            break;
        case 'd':
            Wavetable.fill(mVCO1.get_wavetable(), Wellen.WAVEFORM_SAWTOOTH);
            break;
        case 'f':
            Wavetable.fill(mVCO1.get_wavetable(), Wellen.WAVEFORM_SQUARE);
            break;
        case '1':
            mAnalogFilter.settype(RRAnalogFilter.TYPE_LPF_1_POLE);
            break;
        case '2':
            mAnalogFilter.settype(RRAnalogFilter.TYPE_HPF_1_POLE);
            break;
        case '3':
            mAnalogFilter.settype(RRAnalogFilter.TYPE_LPF_2_POLE);
            break;
        case '4':
            mAnalogFilter.settype(RRAnalogFilter.TYPE_HPF_2_POLE);
            break;
        case '5':
            mAnalogFilter.settype(RRAnalogFilter.TYPE_BPF_2_POLE);
            break;
        case '6':
            mAnalogFilter.settype(RRAnalogFilter.TYPE_NOTCH_2_POLE);
            break;
        case '7':
            mAnalogFilter.settype(RRAnalogFilter.TYPE_PEAK_2_POLE);
            break;
    }
}
void audioblock(float[] output_signal) {
    for (int i = 0; i < output_signal.length; i++) {
        final float a = mVCO1.output();
        final float b = mVCO2.output();
        output_signal[i] = a + b;
        output_signal[i] *= 0.5f;
        output_signal[i] *= mMasterVolume;
    }
    mAnalogFilter.filterout(output_signal);
}

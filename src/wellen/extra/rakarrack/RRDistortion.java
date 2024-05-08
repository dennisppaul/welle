/*
 * Wellen
 *
 * This file is part of the *wellen* library (https://github.com/dennisppaul/wellen).
 * Copyright (c) 2024 Dennis P Paul.
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, version 3.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package wellen.extra.rakarrack;

/*
  ZynAddSubFX - a software synthesizer

  Distorsion.h - Distorsion Effect
  Copyright (C) 2002-2005 Nasca Octavian Paul
  Author: Nasca Octavian Paul

  Modified for rakarrack by Josep Andreu & Hernan Ordiales & Ryan Billing

  This program is free software; you can redistribute it and/or modify
  it under the terms of version 2 of the GNU General Public License
  as published by the Free Software Foundation.

  This program is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License (version 2) for more details.

  You should have received a copy of the GNU General Public License (version 2)
  along with this program; if not, write to the Free Software Foundation,
  Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307 USA

*/

import wellen.dsp.EffectMono;
import wellen.dsp.EffectStereo;

import static wellen.extra.rakarrack.RRAnalogFilter.TYPE_HPF_2_POLE;
import static wellen.extra.rakarrack.RRAnalogFilter.TYPE_LPF_2_POLE;
import static wellen.extra.rakarrack.RRWaveShaper.TYPE_ARCTANGENT;
import static wellen.extra.rakarrack.RRWaveShaper.TYPE_ASYMETRIC;
import static wellen.extra.rakarrack.RRWaveShaper.TYPE_POW;
import static wellen.extra.rakarrack.RRWaveShaper.TYPE_SIGMOID;

public class RRDistortion implements EffectMono, EffectStereo {
    public static final int NUM_PRESETS = 6;
    public static final int PARAM_STEREO = 9;
    public static final int PRESET_DISTORSION_1 = 2;
    public static final int PRESET_DISTORSION_2 = 3;
    public static final int PRESET_DISTORSION_3 = 4;
    public static final int PRESET_GUITAR_AMP = 5;
    public static final int PRESET_OVERDRIVE_1 = 0;
    public static final int PRESET_OVERDRIVE_2 = 1;
    private final RRAnalogFilter DCl;
    private final RRAnalogFilter DCr;
    private int Pdrive;             //the input amplification
    private int Phpf;               //highpass filter
    private int Plevel;             //the ouput amplification
    private int Plpf;               //lowpass filter
    private int Plrcross;           // L/R Mixing
    private int Pnegate;            //if the input is negated
    private int Poctave;            //mix sub octave
    private int Ppanning;           //Panning
    private boolean Pprefiltering;  //if you want to do the filtering before the distorsion
    private int Ppreset;
    private boolean Pstereo;        //false=mono,true=stereo
    private int Ptype;              //Distorsion type
    private int Pvolume;            //Volumul or E/R
    private final RRAnalogFilter blockDCl;
    private final RRAnalogFilter blockDCr;
    private final RRWaveShaper dwshapel;
    private final RRWaveShaper dwshaper;
    private final RRAnalogFilter hpfl;
    private final RRAnalogFilter hpfr;
    private final RRAnalogFilter lpfl;
    private final RRAnalogFilter lpfr;
    private float lrcross;
    private float octave_memoryl;
    private float octave_memoryr;
    private float octmix;
    private final float[] octoutl;
    private final float[] octoutr;
    private float outvolume;
    private float panning;
    private float togglel;
    private float toggler;
    public RRDistortion() {
        octoutl = new float[RRUtilities.PERIOD];
        octoutr = new float[RRUtilities.PERIOD];

        lpfl = new RRAnalogFilter(TYPE_LPF_2_POLE, 22000, 1, 0);
        lpfr = new RRAnalogFilter(TYPE_LPF_2_POLE, 22000, 1, 0);
        hpfl = new RRAnalogFilter(TYPE_HPF_2_POLE, 20, 1, 0);
        hpfr = new RRAnalogFilter(TYPE_HPF_2_POLE, 20, 1, 0);
        blockDCl = new RRAnalogFilter(TYPE_LPF_2_POLE, 440.0f, 1, 0);
        blockDCr = new RRAnalogFilter(TYPE_LPF_2_POLE, 440.0f, 1, 0);
        blockDCl.setfreq(75.0f);
        blockDCr.setfreq(75.0f);
        DCl = new RRAnalogFilter(TYPE_HPF_2_POLE, 30, 1, 0);
        DCr = new RRAnalogFilter(TYPE_HPF_2_POLE, 30, 1, 0);
        DCl.setfreq(30.0f);
        DCr.setfreq(30.0f);

        dwshapel = new RRWaveShaper();
        dwshaper = new RRWaveShaper();

        //default values
        Ppreset = 0;
        Pvolume = 50;
        Plrcross = 40;
        Pdrive = 90;
        Plevel = 64;
        Ptype = 0;
        Pnegate = 0;
        Plpf = 127;
        Phpf = 0;
        Pstereo = false;
        Pprefiltering = false;
        Poctave = 0;
        togglel = 1.0f;
        octave_memoryl = -1.0f;
        toggler = 1.0f;
        octave_memoryr = -1.0f;
        octmix = 0.0f;

        setpreset(Ppreset);
        cleanup();
    }

    public void setpreset(int npreset) {
        int[][] presets = {
                //Overdrive 1
                {84, 64, 35, 56, 40, TYPE_ARCTANGENT, 0, 6703, 21, 0, 0},
                //Overdrive 2
                {85, 64, 35, 29, 45, TYPE_ASYMETRIC, 0, 25040, 21, 0, 0},
                //Distorsion 1
                {0, 64, 0, 87, 14, 6, TYPE_ARCTANGENT, 3134, 157, 0, 1},
                //Distorsion 2
                {0, 64, 127, 87, 14, TYPE_ARCTANGENT, 1, 3134, 102, 0, 0},
                //Distorsion 3
                {0, 64, 127, 127, 12, TYPE_SIGMOID, 0, 5078, 56, 0, 1},
                //Guitar Amp
                {84, 64, 35, 63, 50, TYPE_POW, 0, 824, 21, 0, 0}};
        for (int n = 0; n < presets[npreset].length; n++) {
            changepar(n, presets[npreset][n]);
        }

        Ppreset = npreset;
        cleanup();
    }

    public void out(float[] smpsl, float[] smpsr) {
        final float[] efxoutl = smpsl;
//        // @TODO(optimize this and do not process right channel when not in stereo mode)
//        final float[] efxoutr = (smpsr == null) ? new float[efxoutl.length] : smpsr;
        final float[] efxoutr = smpsr;
        float inputvol = RRUtilities.powf(5.0f, ((float) Pdrive - 32.0f) / 127.0f);

        if (Pnegate != 0) {
            inputvol *= -1.0f;
        }
        if (Pstereo) {
            for (int i = 0; i < RRUtilities.PERIOD; i++) {
                efxoutl[i] *= inputvol * 2.0f;
                efxoutr[i] *= inputvol * 2.0f;
            }
        } else {
            for (int i = 0; i < RRUtilities.PERIOD; i++) {
                efxoutl[i] = (efxoutl[i] + (efxoutr == null ? 0.0f : efxoutr[i])) * inputvol;
            }
        }

        if (Pprefiltering) {
            applyfilters(efxoutl, efxoutr);
        }

        dwshapel.waveshapesmps(RRUtilities.PERIOD, efxoutl, Ptype, Pdrive, true);
        if (efxoutr != null && Pstereo) {
            dwshaper.waveshapesmps(RRUtilities.PERIOD, efxoutr, Ptype, Pdrive, true);
        }

        if (!Pprefiltering) {
            applyfilters(efxoutl, efxoutr);
        }

        if (efxoutr != null && !Pstereo) {
            RRUtilities.memcpy(efxoutr, efxoutl, efxoutr.length);
        }

        if (octmix > 0.01f) {
            for (int i = 0; i < RRUtilities.PERIOD; i++) {
                float lout = efxoutl[i];
                if ((octave_memoryl < 0.0f) && (lout > 0.0f)) {
                    togglel *= -1.0f;
                }
                octave_memoryl = lout;
                octoutl[i] = lout * togglel;

                if (efxoutr != null) {
                    float rout = efxoutr[i];
                    if ((octave_memoryr < 0.0f) && (rout > 0.0f)) {
                        toggler *= -1.0f;
                    }
                    octave_memoryr = rout;
                    octoutr[i] = rout * toggler;
                }
            }

            blockDCl.filterout(octoutl);
            if (efxoutr != null) {
                blockDCr.filterout(octoutr);
            }
        }

        float level = RRUtilities.dB2rap(60.0f * (float) Plevel / 127.0f - 40.0f);

        for (int i = 0; i < RRUtilities.PERIOD; i++) {
            float lout = efxoutl[i];
            float rout = (efxoutr != null) ? efxoutr[i] : lout;

            float l = lout * (1.0f - lrcross) + rout * lrcross;
            float r = rout * (1.0f - lrcross) + lout * lrcross;

            if (octmix > 0.01f) {
                lout = l * (1.0f - octmix) + octoutl[i] * octmix;
                rout = r * (1.0f - octmix) + octoutr[i] * octmix;
            } else {
                lout = l;
                rout = r;
            }

            efxoutl[i] = lout * 2.0f * level * panning;
            if (efxoutr != null) {
                efxoutr[i] = rout * 2.0f * level * (1.0f - panning);
            }
        }

        DCl.filterout(efxoutl);
        if (efxoutr != null) {
            DCr.filterout(efxoutr);
        }

        if (efxoutr != null) {
            RRUtilities.memcpy(efxoutr, efxoutl, efxoutl.length);
        }
    }

    public void out(float[] smpsl) {
        out(smpsl, null);
    }

    public void setoctave(int pPoctave) {
        Poctave = pPoctave;
        octmix = (float) (Poctave) / 127.0f;
    }

    public void changepar(int npar, int value) {
        switch (npar) {
            case 0:
                setvolume(value);
                break;
            case 1:
                setpanning(value);
                break;
            case 2:
                setlrcross(value);
                break;
            case 3:
                Pdrive = value;
                break;
            case 4:
                Plevel = value;
                break;
            case 5:
                Ptype = value;
                break;
            case 6:
                if (value > 1) {
                    value = 1;
                }
                Pnegate = value;
                break;
            case 7:
                setlpf(value);
                break;
            case 8:
                sethpf(value);
                break;
            case PARAM_STEREO:
                if (value > 1) {
                    value = 1;
                }
                Pstereo = (value != 0);
                break;
            case 10:
                Pprefiltering = value > 0;
                break;
            case 11:
                break;
            case 12:
                setoctave(value);
                break;
        }
    }

    public void setvolume(int Pvolume) {
        this.Pvolume = Pvolume;

        outvolume = (float) Pvolume / 127.0f;
        if (Pvolume == 0) {
            cleanup();
        }

    }

    public void setpanning(int Ppanning) {
        this.Ppanning = Ppanning;
        panning = ((float) Ppanning + 0.5f) / 127.0f;
    }

    public void setlrcross(int Plrcross) {
        this.Plrcross = Plrcross;
        lrcross = (float) Plrcross / 127.0f * 1.0f;
    }

    public void setlpf(int value) {
        Plpf = value;
        float fr = (float) Plpf;
        lpfl.setfreq(fr);
        lpfr.setfreq(fr);
    }

    public void sethpf(int value) {
        Phpf = value;
        float fr = (float) Phpf;

        hpfl.setfreq(fr);
        hpfr.setfreq(fr);
        //Prefiltering of 51 is approx 630 Hz. 50 - 60 generally good for OD pedal.
    }

    public int getpar(int npar) {
        switch (npar) {
            case 0:
                return (Pvolume);
            case 1:
                return (Ppanning);
            case 2:
                return (Plrcross);
            case 3:
                return (Pdrive);
            case 4:
                return (Plevel);
            case 5:
                return (Ptype);
            case 6:
                return (Pnegate);
            case 7:
                return (Plpf);
            case 8:
                return (Phpf);
            case PARAM_STEREO:
                return (Pstereo ? 1 : 0);
            case 10:
                return (Pprefiltering ? 1 : 0);
            case 11:
                return (0);
            case 12:
                return (Poctave);
        }
        return (0);            //in case of bogus parameter number
    }

    public void cleanup() {
        lpfl.cleanup();
        hpfl.cleanup();
        lpfr.cleanup();
        hpfr.cleanup();
        blockDCr.cleanup();
        blockDCl.cleanup();
        DCl.cleanup();
        DCr.cleanup();
    }

    private void applyfilters(float[] efxoutl, float[] efxoutr) {
        lpfl.filterout(efxoutl);
        hpfl.filterout(efxoutl);

        if (Pstereo && efxoutr != null) {
            lpfr.filterout(efxoutr);
            hpfr.filterout(efxoutr);
        }
    }
}

package Engine;

import Engine.Piece.*;
import Engine.Piece.General.General;
import GUI.Coords;

import java.util.HashMap;

public enum Alliance {

    RED{
        @Override
        public String toString() {
            return "Red";
        }

        @Override
        public boolean inMyTerritory(Coords c){
            int q = c.getQ();
            int s = c.getS();
            int r = c.getR();

            return (((s <= -2 && s >= -10) && (r <= -2 && r >= -10) && (q == 12)) ||
                    ((s <= -2 && s >= -11) && (r <= -2 && r >= -11) && (q == 13)) ||
                    ((s <= -2 && s >= -12) && (r <= -2 && r >= -12) && (q == 14)) ||
                    ((s <= -2 && s >= -13) && (r <= -2 && r >= -13) && (q == 15)) ||
                    ((s <= -2 && s >= -14) && (r <= -2 && r >= -14) && (q == 16)) ||
                    ((s <= -2 && s >= -15) && (r <= -2 && r >= -15) && (q == 17)) ||
                    ((s <= -3 && s >= -15) && (r <= -3 && r >= -15) && (q == 18)) ||
                    ((s <= -4 && s >= -15) && (r <= -4 && r >= -15) && (q == 19)) ||
                    ((s <= -5 && s >= -15) && (r <= -5 && r >= -15) && (q == 20)));
        }

        @Override
        public HashMap<Coords, Parcel> getInitialSetup(){
            HashMap<Coords, Parcel> setup = new HashMap<>();
            Coords c1 = new Coords(16, -8, -8);
            Parcel p1 = new Parcel(new Capitol(c1, this, true));
            setup.put(c1, p1);
            Coords c2 = new Coords(14, -7, -7);
            Parcel p2 = new Parcel(new Town(c2, this));
            setup.put(c2, p2);
            Coords c3 = new Coords(18, -9, -9);
            Parcel p3 = new Parcel(new Town(c3, this));
            setup.put(c3, p3);
            Coords c4 = new Coords(16, -11, -5);
            Parcel p4 = new Parcel(new Town(c4, this));
            setup.put(c4, p4);
            Coords c5 = new Coords(16, -5, -11);
            Parcel p5 = new Parcel(new Town(c5, this));
            setup.put(c5, p5);
            Coords c6 = new Coords(16, -6, -10);
            Parcel p6 = new Parcel(new Supply(c6,this));
            setup.put(c6, p6);
            Coords c7 = new Coords(16, -7, -9);
            Parcel p7 = new Parcel(new Supply(c7,this));
            setup.put(c7, p7);
            Coords c8 = new Coords(16, -9, -7);
            Parcel p8 = new Parcel(new Supply(c8,this));
            setup.put(c8, p8);
            Coords c9 = new Coords(16, -10, -6);
            Parcel p9 = new Parcel(new Supply(c9,this));
            setup.put(c9, p9);
            Coords c10 = new Coords(17, -8, -9);
            Parcel p10 = new Parcel(new Supply(c10,this));
            setup.put(c10, p10);
            Coords c11 = new Coords(17, -9, -8);
            Parcel p11 = new Parcel(new Supply(c11,this));
            setup.put(c11, p11);
            Coords c12 = new Coords(15, -7, -8);
            Parcel p12 = new Parcel(new Supply(c12,this));
            setup.put(c12, p12);
            Coords c13 = new Coords(15, -8, -7);
            Parcel p13 = new Parcel(new Supply(c13,this));
            setup.put(c13, p13);
            Coords c14 = new Coords(13, -6, -7);
            Parcel p14 = new Parcel(new Supply(c14,this));
            setup.put(c14, p14);
            Coords c15 = new Coords(13, -7, -6);
            Parcel p15 = new Parcel(new Supply(c15,this));
            setup.put(c15, p15);
            Coords c16 = new Coords(12, -8, -4);
            Parcel p16 = new Parcel(new General(c16, 1, this, 0));
            setup.put(c16, p16);
            Coords c17 = new Coords(12, -7, -5);
            Parcel p17 = new Parcel(new General(c17, 2, this, 0));
            setup.put(c17, p17);
            Coords c18 = new Coords(12, -6, -6);
            Parcel p18 = new Parcel(new General(c18, 3, this, 0));
            setup.put(c18, p18);
            Coords c19 = new Coords(12, -5, -7);
            Parcel p19 = new Parcel(new General(c19, 4, this, 0));
            setup.put(c19, p19);
            Coords c20 = new Coords(12, -4, -8);
            Parcel p20 = new Parcel(new General(c20, 5, this, 0));
            setup.put(c20, p20);
            return setup;
        }

        @Override
        public int getDataCode(){
            return 1;
        }
    },

    YELLOW{
        @Override
        public String toString() {
            return "Yellow";
        }

        @Override
        public boolean inMyTerritory(Coords c){
            int q = c.getQ();
            int s = c.getS();
            int r = c.getR();

            return (((q <= -2 && q >= -10) && (r <= -2 && r >= -10) && (s == 12)) ||
                    ((q <= -2 && q >= -11) && (r <= -2 && r >= -11) && (s == 13)) ||
                    ((q <= -2 && q >= -12) && (r <= -2 && r >= -12) && (s == 14)) ||
                    ((q <= -2 && q >= -13) && (r <= -2 && r >= -13) && (s == 15)) ||
                    ((q <= -2 && q >= -14) && (r <= -2 && r >= -14) && (s == 16)) ||
                    ((q <= -2 && q >= -15) && (r <= -2 && r >= -15) && (s == 17)) ||
                    ((q <= -3 && q >= -15) && (r <= -3 && r >= -15) && (s == 18)) ||
                    ((q <= -4 && q >= -15) && (r <= -4 && r >= -15) && (s == 19)) ||
                    ((q <= -5 && q >= -15) && (r <= -5 && r >= -15) && (s == 20)));
        }

        @Override
        public HashMap<Coords, Parcel> getInitialSetup(){
            HashMap<Coords, Parcel> setup = new HashMap<>();
            Coords c1 = new Coords(-8, -8, 16);
            Parcel p1 = new Parcel(new Capitol(c1, this, true));
            setup.put(c1, p1);
            Coords c2 = new Coords(-7, -7, 14);
            Parcel p2 = new Parcel(new Town(c2,this));
            setup.put(c2, p2);
            Coords c3 = new Coords(-9, -9, 18);
            Parcel p3 = new Parcel(new Town(c3,this));
            setup.put(c3, p3);
            Coords c4 = new Coords(-5, -11, 16);
            Parcel p4 = new Parcel(new Town(c4,this));
            setup.put(c4, p4);
            Coords c5 = new Coords(-11, -5, 16);
            Parcel p5 = new Parcel(new Town(c5,this));
            setup.put(c5, p5);
            Coords c6 = new Coords(-6, -10, 16);
            Parcel p6 = new Parcel(new Supply(c6,this));
            setup.put(c6, p6);
            Coords c7 = new Coords(-7, -9, 16);
            Parcel p7 = new Parcel(new Supply(c7,this));
            setup.put(c7, p7);
            Coords c8 = new Coords(-9, -7, 16);
            Parcel p8 = new Parcel(new Supply(c8,this));
            setup.put(c8, p8);
            Coords c9 = new Coords(-10, -6, 16);
            Parcel p9 = new Parcel(new Supply(c9, this));
            setup.put(c9, p9);
            Coords c10 = new Coords(-8, -9, 17);
            Parcel p10 = new Parcel(new Supply(c10, this));
            setup.put(c10, p10);
            Coords c11 = new Coords(-9, -8, 17);
            Parcel p11 = new Parcel(new Supply(c11, this));
            setup.put(c11, p11);
            Coords c12 = new Coords(-7, -8, 15);
            Parcel p12 = new Parcel(new Supply(c12, this));
            setup.put(c12, p12);
            Coords c13 = new Coords(-8, -7, 15);
            Parcel p13 = new Parcel(new Supply(c13, this));
            setup.put(c13, p13);
            Coords c14 = new Coords(-6, -7, 13);
            Parcel p14 = new Parcel(new Supply(c14, this));
            setup.put(c14, p14);
            Coords c15 = new Coords(-7, -6, 13);
            Parcel p15 = new Parcel(new Supply(c15, this));
            setup.put(c15, p15);
            Coords c16 = new Coords(-8, -4, 12);
            Parcel p16 = new Parcel(new General(c16, 1, this, 0));
            setup.put(c16, p16);
            Coords c17 = new Coords(-7, -5, 12);
            Parcel p17 = new Parcel(new General(c17, 2, this, 0));
            setup.put(c17, p17);
            Coords c18 = new Coords(-6, -6, 12);
            Parcel p18 = new Parcel(new General(c18, 3, this, 0));
            setup.put(c18, p18);
            Coords c19 = new Coords(-5, -7, 12);
            Parcel p19 = new Parcel(new General(c19, 4, this, 0));
            setup.put(c19, p19);
            Coords c20 = new Coords(-4, -8, 12);
            Parcel p20 = new Parcel(new General(c20, 5, this, 0));
            setup.put(c20, p20);
            return setup;
        }

        @Override
        public int getDataCode(){
            return 3;
        }

    },

    BLUE{
        @Override
        public String toString() {
            return "Blue";
        }

        @Override
        public boolean inMyTerritory(Coords c){
            int q = c.getQ();
            int s = c.getS();
            int r = c.getR();

            return (((q <= -2 && q >= -10) && (s <= -2 && s >= -10) && (r == 12)) ||
                    ((q <= -2 && q >= -11) && (s <= -2 && s >= -11) && (r == 13)) ||
                    ((q <= -2 && q >= -12) && (s <= -2 && s >= -12) && (r == 14)) ||
                    ((q <= -2 && q >= -13) && (s <= -2 && s >= -13) && (r == 15)) ||
                    ((q <= -2 && q >= -14) && (s <= -2 && s >= -14) && (r == 16)) ||
                    ((q <= -2 && q >= -15) && (s <= -2 && s >= -15) && (r == 17)) ||
                    ((q <= -3 && q >= -15) && (s <= -3 && s >= -15) && (r == 18)) ||
                    ((q <= -4 && q >= -15) && (s <= -4 && s >= -15) && (r == 19)) ||
                    ((q <= -5 && q >= -15) && (s <= -5 && s >= -15) && (r == 20)));
        }

        @Override
        public HashMap<Coords, Parcel> getInitialSetup(){
            HashMap<Coords, Parcel> setup = new HashMap<>();
            Coords c1 = new Coords(-8, 16, -8);
            Parcel p1 = new Parcel(new Capitol(c1, this, true));
            setup.put(c1, p1);
            Coords c2 = new Coords(-7, 14, -7);
            Parcel p2 = new Parcel(new Town(c2,this));
            setup.put(c2, p2);
            Coords c3 = new Coords(-9, 18, -9);
            Parcel p3 = new Parcel(new Town(c3,this));
            setup.put(c3, p3);
            Coords c4 = new Coords(-5, 16, -11);
            Parcel p4 = new Parcel(new Town(c4,this));
            setup.put(c4, p4);
            Coords c5 = new Coords(-11, 16, -5);
            Parcel p5 = new Parcel(new Town(c5,this));
            setup.put(c5, p5);
            Coords c6 = new Coords(-6, 16, -10);
            Parcel p6 = new Parcel(new Supply(c6, this));
            setup.put(c6, p6);
            Coords c7 = new Coords(-7, 16, -9);
            Parcel p7 = new Parcel(new Supply(c7, this));
            setup.put(c7, p7);
            Coords c8 = new Coords(-9, 16, -7);
            Parcel p8 = new Parcel(new Supply(c8, this));
            setup.put(c8, p8);
            Coords c9 = new Coords(-10, 16, -6);
            Parcel p9 = new Parcel(new Supply(c9,this));
            setup.put(c9, p9);
            Coords c10 = new Coords(-8, 17, -9);
            Parcel p10 = new Parcel(new Supply(c10, this));
            setup.put(c10, p10);
            Coords c11 = new Coords(-9, 17, -8);
            Parcel p11 = new Parcel(new Supply(c11,this));
            setup.put(c11, p11);
            Coords c12 = new Coords(-7, 15, -8);
            Parcel p12 = new Parcel(new Supply(c12, this));
            setup.put(c12, p12);
            Coords c13 = new Coords(-8, 15, -7);
            Parcel p13 = new Parcel(new Supply(c13, this));
            setup.put(c13, p13);
            Coords c14 = new Coords(-6, 13, -7);
            Parcel p14 = new Parcel(new Supply(c14, this));
            setup.put(c14, p14);
            Coords c15 = new Coords(-7, 13, -6);
            Parcel p15 = new Parcel(new Supply(c15, this));
            setup.put(c15, p15);
            Coords c16 = new Coords(-4, 12, -8);
            Parcel p16 = new Parcel(new General(c16, 1, this, 0));
            setup.put(c16, p16);
            Coords c17 = new Coords(-5, 12, -7);
            Parcel p17 = new Parcel(new General(c17, 2, this, 0));
            setup.put(c17, p17);
            Coords c18 = new Coords(-6, 12, -6);
            Parcel p18 = new Parcel(new General(c18, 3, this, 0));
            setup.put(c18, p18);
            Coords c19 = new Coords(-7, 12, -5);
            Parcel p19 = new Parcel(new General(c19, 4, this, 0));
            setup.put(c19, p19);
            Coords c20 = new Coords(-8, 12, -4);
            Parcel p20 = new Parcel(new General(c20, 5, this, 0));
            setup.put(c20, p20);
            return setup;
        }

        @Override
        public int getDataCode(){
            return 5;
        }
    },

    ORANGE{
        @Override
        public String toString() {
            return "Orange";
        }

        @Override
        public boolean inMyTerritory(Coords c){
            int q = c.getQ();
            int s = c.getS();
            int r = c.getR();

            return (((q >= 2 && q <= 10) && (s >= 2 && s <= 10) && (r == -12)) ||
                    ((q >= 2 && q <= 11) && (s >= 2 && s <= 11) && (r == -13)) ||
                    ((q >= 2 && q <= 12) && (s >= 2 && s <= 12) && (r == -14)) ||
                    ((q >= 2 && q <= 13) && (s >= 2 && s <= 13) && (r == -15)) ||
                    ((q >= 2 && q <= 14) && (s >= 2 && s <= 14) && (r == -16)) ||
                    ((q >= 2 && q <= 15) && (s >= 2 && s <= 15) && (r == -17)) ||
                    ((q >= 3 && q <= 15) && (s >= 3 && s <= 15) && (r == -18)) ||
                    ((q >= 4 && q <= 15) && (s >= 4 && s <= 15) && (r == -19)) ||
                    ((q >= 5 && q <= 15) && (s >= 5 && s <= 15) && (r == -20)));
        }

        @Override
        public HashMap<Coords, Parcel> getInitialSetup(){
            HashMap<Coords, Parcel> setup = new HashMap<>();
            Coords c1 = new Coords(8, -16, 8);
            Parcel p1 = new Parcel(new Capitol(c1, this, true));
            setup.put(c1, p1);
            Coords c2 = new Coords(7, -14, 7);
            Parcel p2 = new Parcel(new Town(c2, this));
            setup.put(c2, p2);
            Coords c3 = new Coords(9, -18, 9);
            Parcel p3 = new Parcel(new Town(c3, this));
            setup.put(c3, p3);
            Coords c4 = new Coords(5, -16, 11);
            Parcel p4 = new Parcel(new Town(c4, this));
            setup.put(c4, p4);
            Coords c5 = new Coords(11, -16, 5);
            Parcel p5 = new Parcel(new Town(c5, this));
            setup.put(c5, p5);
            Coords c6 = new Coords(6, -16, 10);
            Parcel p6 = new Parcel(new Supply(c6, this));
            setup.put(c6, p6);
            Coords c7 = new Coords(7, -16, 9);
            Parcel p7 = new Parcel(new Supply(c7, this));
            setup.put(c7, p7);
            Coords c8 = new Coords(9, -16, 7);
            Parcel p8 = new Parcel(new Supply(c8, this));
            setup.put(c8, p8);
            Coords c9 = new Coords(10, -16, 6);
            Parcel p9 = new Parcel(new Supply(c9, this));
            setup.put(c9, p9);
            Coords c10 = new Coords(8, -17, 9);
            Parcel p10 = new Parcel(new Supply(c10, this));
            setup.put(c10, p10);
            Coords c11 = new Coords(9, -17, 8);
            Parcel p11 = new Parcel(new Supply(c11, this));
            setup.put(c11, p11);
            Coords c12 = new Coords(7, -15, 8);
            Parcel p12 = new Parcel(new Supply(c12, this));
            setup.put(c12, p12);
            Coords c13 = new Coords(8, -15, 7);
            Parcel p13 = new Parcel(new Supply(c13, this));
            setup.put(c13, p13);
            Coords c14 = new Coords(6, -13, 7);
            Parcel p14 = new Parcel(new Supply(c14, this));
            setup.put(c14, p14);
            Coords c15 = new Coords(7, -13, 6);
            Parcel p15 = new Parcel(new Supply(c15, this));
            setup.put(c15, p15);
            Coords c16 = new Coords(4, -12, 8);
            Parcel p16 = new Parcel(new General(c16, 1, this, 0));
            setup.put(c16, p16);
            Coords c17 = new Coords(5, -12, 7);
            Parcel p17 = new Parcel(new General(c17, 2, this, 0));
            setup.put(c17, p17);
            Coords c18 = new Coords(6, -12, 6);
            Parcel p18 = new Parcel(new General(c18, 3, this, 0));
            setup.put(c18, p18);
            Coords c19 = new Coords(7, -12, 5);
            Parcel p19 = new Parcel(new General(c19, 4, this, 0));
            setup.put(c19, p19);
            Coords c20 = new Coords(8, -12, 4);
            Parcel p20 = new Parcel(new General(c20, 5, this, 0));
            setup.put(c20, p20);
            return setup;
        }

        @Override
        public int getDataCode(){
            return 2;
        }
    },

    GREEN{
        @Override
        public String toString() {
            return "Green";
        }

        @Override
        public boolean inMyTerritory(Coords c){
            int q = c.getQ();
            int s = c.getS();
            int r = c.getR();

            return (((s >= 2 && s <= 10) && (r >= 2 && r <= 10) && (q == -12)) ||
                    ((s >= 2 && s <= 11) && (r >= 2 && r <= 11) && (q == -13)) ||
                    ((s >= 2 && s <= 12) && (r >= 2 && r <= 12) && (q == -14)) ||
                    ((s >= 2 && s <= 13) && (r >= 2 && r <= 13) && (q == -15)) ||
                    ((s >= 2 && s <= 14) && (r >= 2 && r <= 14) && (q == -16)) ||
                    ((s >= 2 && s <= 15) && (r >= 2 && r <= 15) && (q == -17)) ||
                    ((s >= 3 && s <= 15) && (r >= 3 && r <= 15) && (q == -18)) ||
                    ((s >= 4 && s <= 15) && (r >= 4 && r <= 15) && (q == -19)) ||
                    ((s >= 5 && s <= 15) && (r >= 5 && r <= 15) && (q == -20)));

        }

        @Override
        public HashMap<Coords, Parcel> getInitialSetup(){
            HashMap<Coords, Parcel> setup = new HashMap<>();
            Coords c1 = new Coords(-16, 8, 8);
            Parcel p1 = new Parcel(new Capitol(c1, this, true));
            setup.put(c1, p1);
            Coords c2 = new Coords(-14, 7, 7);
            Parcel p2 = new Parcel(new Town(c2, this));
            setup.put(c2, p2);
            Coords c3 = new Coords(-18, 9, 9);
            Parcel p3 = new Parcel(new Town(c3, this));
            setup.put(c3, p3);
            Coords c4 = new Coords(-16, 11, 5);
            Parcel p4 = new Parcel(new Town(c4, this));
            setup.put(c4, p4);
            Coords c5 = new Coords(-16, 5, 11);
            Parcel p5 = new Parcel(new Town(c5, this));
            setup.put(c5, p5);
            Coords c6 = new Coords(-16, 10, 6);
            Parcel p6 = new Parcel(new Supply(c6, this));
            setup.put(c6, p6);
            Coords c7 = new Coords(-16, 9, 7);
            Parcel p7 = new Parcel(new Supply(c7,  this));
            setup.put(c7, p7);
            Coords c8 = new Coords(-16, 7, 9);
            Parcel p8 = new Parcel(new Supply(c8, this));
            setup.put(c8, p8);
            Coords c9 = new Coords(-16, 6, 10);
            Parcel p9 = new Parcel(new Supply(c9,this));
            setup.put(c9, p9);
            Coords c10 = new Coords(-17, 9, 8);
            Parcel p10 = new Parcel(new Supply(c10,this));
            setup.put(c10, p10);
            Coords c11 = new Coords(-17, 8, 9);
            Parcel p11 = new Parcel(new Supply(c11, this));
            setup.put(c11, p11);
            Coords c12 = new Coords(-15, 8, 7);
            Parcel p12 = new Parcel(new Supply(c12, this));
            setup.put(c12, p12);
            Coords c13 = new Coords(-15, 7, 8);
            Parcel p13 = new Parcel(new Supply(c13, this));
            setup.put(c13, p13);
            Coords c14 = new Coords(-13, 7, 6);
            Parcel p14 = new Parcel(new Supply(c14, this));
            setup.put(c14, p14);
            Coords c15 = new Coords(-13, 6, 7);
            Parcel p15 = new Parcel(new Supply(c15, this));
            setup.put(c15, p15);
            Coords c16 = new Coords(-12, 8, 4);
            Parcel p16 = new Parcel(new General(c16, 1,  this, 0));
            setup.put(c16, p16);
            Coords c17 = new Coords(-12, 7, 5);
            Parcel p17 = new Parcel(new General(c17,2,  this, 0));
            setup.put(c17, p17);
            Coords c18 = new Coords(-12, 6, 6);
            Parcel p18 = new Parcel(new General(c18,3,  this, 0));
            setup.put(c18, p18);
            Coords c19 = new Coords(-12, 5, 7);
            Parcel p19 = new Parcel(new General(c19,4,  this, 0));
            setup.put(c19, p19);
            Coords c20 = new Coords(-12, 4, 8);
            Parcel p20 = new Parcel(new General(c20, 5, this, 0));
            setup.put(c20, p20);
            return setup;
        }

        @Override
        public int getDataCode(){
            return 4;
        }
    },

    PURPLE{
        @Override
        public String toString() {
            return "Purple";
        }

        @Override
        public boolean inMyTerritory(Coords c){
            int q = c.getQ();
            int r = c.getR();
            int s = c.getS();
            return (((q >= 2 && q <= 10) && (r >= 2 && r <= 10) && (s == -12)) ||
                    ((q >= 2 && q <= 11) && (r >= 2 && r <= 11) && (s == -13)) ||
                    ((q >= 2 && q <= 12) && (r >= 2 && r <= 12) && (s == -14)) ||
                    ((q >= 2 && q <= 13) && (r >= 2 && r <= 13) && (s == -15)) ||
                    ((q >= 2 && q <= 14) && (r >= 2 && r <= 14) && (s == -16)) ||
                    ((q >= 2 && q <= 15) && (r >= 2 && r <= 15) && (s == -17)) ||
                    ((q >= 3 && q <= 15) && (r >= 3 && r <= 15) && (s == -18)) ||
                    ((q >= 4 && q <= 15) && (r >= 4 && r <= 15) && (s == -19)) ||
                    ((q >= 5 && q <= 15) && (r >= 5 && r <= 15) && (s == -20)));
        }

        @Override
        public HashMap<Coords, Parcel> getInitialSetup(){
            HashMap<Coords, Parcel> setup = new HashMap<>();
            Coords c1 = new Coords(8, 8, -16);
            Parcel p1 = new Parcel(new Capitol(c1, this, true));
            setup.put(c1, p1);
            Coords c2 = new Coords(7, 7, -14);
            Parcel p2 = new Parcel(new Town(c2,this));
            setup.put(c2, p2);
            Coords c3 = new Coords(9, 9, -18);
            Parcel p3 = new Parcel(new Town(c3,this));
            setup.put(c3, p3);
            Coords c4 = new Coords(5, 11, -16);
            Parcel p4 = new Parcel(new Town(c4,this));
            setup.put(c4, p4);
            Coords c5 = new Coords(11, 5, -16);
            Parcel p5 = new Parcel(new Town(c5,this));
            setup.put(c5, p5);
            Coords c6 = new Coords(6, 10, -16);
            Parcel p6 = new Parcel(new Supply(c6,this));
            setup.put(c6, p6);
            Coords c7 = new Coords(7, 9, -16);
            Parcel p7 = new Parcel(new Supply(c7,this));
            setup.put(c7, p7);
            Coords c8 = new Coords(9, 7, -16);
            Parcel p8 = new Parcel(new Supply(c8,this));
            setup.put(c8, p8);
            Coords c9 = new Coords(10, 6, -16);
            Parcel p9 = new Parcel(new Supply(c9,this));
            setup.put(c9, p9);
            Coords c10 = new Coords(8, 9, -17);
            Parcel p10 = new Parcel(new Supply(c10,this));
            setup.put(c10, p10);
            Coords c11 = new Coords(9, 8, -17);
            Parcel p11 = new Parcel(new Supply(c11,this));
            setup.put(c11, p11);
            Coords c12 = new Coords(7, 6, -13);
            Parcel p12 = new Parcel(new Supply(c12,this));
            setup.put(c12, p12);
            Coords c13 = new Coords(7, 8, -15);
            Parcel p13 = new Parcel(new Supply(c13,this));
            setup.put(c13, p13);
            Coords c14 = new Coords(8, 7, -15);
            Parcel p14 = new Parcel(new Supply(c14,this));
            setup.put(c14, p14);
            Coords c15 = new Coords(6, 7, -13);
            Parcel p15 = new Parcel(new Supply(c15,this));
            setup.put(c15, p15);
            Coords c16 = new Coords(8, 4, -12);
            Parcel p16 = new Parcel(new General(c16, 1,this, 0));
            setup.put(c16, p16);
            Coords c17 = new Coords(7, 5, -12);
            Parcel p17 = new Parcel(new General(c17, 2,this, 0));
            setup.put(c17, p17);
            Coords c18 = new Coords(6, 6, -12);
            Parcel p18 = new Parcel(new General(c18, 3,this, 0));
            setup.put(c18, p18);
            Coords c19 = new Coords(5, 7, -12);
            Parcel p19 = new Parcel(new General(c19, 4,this, 0));
            setup.put(c19, p19);
            Coords c20 = new Coords(4, 8, -12);
            Parcel p20 = new Parcel(new General(c20, 5,this, 0));
            setup.put(c20, p20);
            return setup;
        }

        @Override
        public int getDataCode(){
            return 6;
        }
    },

    UNOCCUPIED{
        @Override
        public String toString() {
            return "Unoccupied";
        }

        @Override
        public boolean inMyTerritory(Coords c){
            return false;
        }

        @Override
        public HashMap<Coords, Parcel> getInitialSetup(){
            HashMap<Coords, Parcel> setup = new HashMap<>();
            Coords c1 = new Coords(-4, -4, 8);
            Parcel p1 = new Parcel(new Town(c1,this));
            setup.put(c1, p1);
            Coords c2 = new Coords(-8, 4, 4);
            Parcel p2 = new Parcel(new Town(c2,this));
            setup.put(c2, p2);
            Coords c3 = new Coords(-4, 0, 4);
            Parcel p3 = new Parcel(new Town(c3,this));
            setup.put(c3, p3);
            Coords c4 = new Coords(0, -4, 4);
            Parcel p4 = new Parcel(new Town(c4,this));
            setup.put(c4, p4);
            Coords c5 = new Coords(4, -8, 4);
            Parcel p5 = new Parcel(new Town(c5,this));
            setup.put(c5, p5);
            Coords c6 = new Coords(-4, 4, 0);
            Parcel p6 = new Parcel(new Town(c6,this));
            setup.put(c6, p6);
            Coords c7 = new Coords(4, -4, 0);
            Parcel p7 = new Parcel(new Town(c7,this));
            setup.put(c7, p7);
            Coords c8 = new Coords(-4, 8, -4);
            Parcel p8 = new Parcel(new Town(c8,this));
            setup.put(c8, p8);
            Coords c9 = new Coords(0, 4, -4);
            Parcel p9 = new Parcel(new Town(c9,this));
            setup.put(c9, p9);
            Coords c10 = new Coords(4, 0, -4);
            Parcel p10 = new Parcel(new Town(c10,this));
            setup.put(c10, p10);
            Coords c11 = new Coords(8, -4, -4);
            Parcel p11 = new Parcel(new Town(c11,this));
            setup.put(c11, p11);
            Coords c12 = new Coords(4, 4, -8);
            Parcel p12 = new Parcel(new Town(c12,this));
            setup.put(c12, p12);

            return setup;
        }

        @Override
        public int getDataCode(){
            return 0;
        }
    },;

    public abstract boolean inMyTerritory(Coords c);

    public abstract HashMap<Coords, Parcel> getInitialSetup();

    public abstract int getDataCode();

    }

package com.github.siyamed.shapeimageview.path.parser;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;

@SuppressWarnings("FinalStaticMethod")
class NumberParse {
    final ArrayList<Float> numbers;
    private final int nextCmd;

    private NumberParse(ArrayList<Float> numbers, int nextCmd) {
        this.numbers = numbers;
        this.nextCmd = nextCmd;
    }

    @SuppressWarnings("unused")
    public int getNextCmd() {
        return nextCmd;
    }

    @SuppressWarnings("unused")
    public float getNumber(int index) {
        return numbers.get(index);
    }

    static final NumberParse getNumberParseAttr(String name, XmlPullParser attributes) {
        int n = attributes.getAttributeCount();
        for (int i = 0; i < n; i++) {
            if (attributes.getAttributeName(i).equals(name)) {
                return NumberParse.parseNumbers(attributes.getAttributeValue(i));
            }
        }
        return null;
    }

    static NumberParse parseNumbers(String s) {
        int n = s.length();
        int p = 0;
        ArrayList<Float> numbers = new ArrayList<Float>();
        boolean skipChar = false;
        for (int i = 1; i < n; i++) {
            if (skipChar) {
                skipChar = false;
                continue;
            }
            char c = s.charAt(i);
            switch (c) {
                // This ends the parsing, as we are on the next element
                case 'M':
                case 'm':
                case 'Z':
                case 'z':
                case 'L':
                case 'l':
                case 'H':
                case 'h':
                case 'V':
                case 'v':
                case 'C':
                case 'c':
                case 'S':
                case 's':
                case 'Q':
                case 'q':
                case 'T':
                case 't':
                case 'a':
                case 'A':
                case ')': {
                    String str = s.substring(p, i);
                    if (str.trim().length() > 0) {
                        Float f = Float.parseFloat(str);
                        numbers.add(f);
                    }
                    p = i;
                    return new NumberParse(numbers, p);
                }
                case '\n':
                case '\t':
                case ' ':
                case ',':{
                    String str = s.substring(p, i);
                    // Just keep moving if multiple whitespace
                    if (str.trim().length() > 0) {
                        Float f = Float.parseFloat(str);
                        numbers.add(f);
                        if (c == '-') {
                            p = i;
                        } else {
                            p = i + 1;
                            skipChar = true;
                        }
                    } else {
                        p++;
                    }
                    break;
                }
            }
        }
        String last = s.substring(p);
        if (last.length() > 0) {
            try {
                numbers.add(Float.parseFloat(last));
            } catch (NumberFormatException nfe) {
                //ignore
            }
            p = s.length();
        }
        return new NumberParse(numbers, p);
    }
}
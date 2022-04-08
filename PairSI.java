class PairSI implements Comparable<PairSI> {
    String word = "";
    int frequency = 1;

    PairSI(String s, int freq) {
        this.word = s;
        this.frequency = freq;
    }

    @Override
    public int compareTo(PairSI o) {
        return this.word.compareTo(o.word);
    }
}

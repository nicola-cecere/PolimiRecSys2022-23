package reebo_cecere.ScoreComputer;

import reebo_cecere.Elements.Interaction;
import reebo_cecere.Elements.Item;
import reebo_cecere.Parser.InteractionReader;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.TreeSet;

public class WithImpressions extends ScoreComputer {
    public WithImpressions(List<Interaction> interactions, boolean only_impressions) {
        super(interactions, null);
        this.interactions = InteractionReader.impressionsToInteractions(interactions, only_impressions);
    }

    @Override
    public void computeAndSaveNewScores(File out) throws IOException {
        ScoreComputer binary = new WithBinaryValues(interactions, null);
        binary.computeAndSaveNewScores(out);
    }
}

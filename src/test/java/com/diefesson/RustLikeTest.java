package com.diefesson;

import static com.diefesson.difcomp.rustlike.RLGrammar.rlGrammar;
import static java.util.stream.Collectors.joining;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.util.List;

import org.junit.jupiter.api.Test;

import com.diefesson.difcomp.error.GrammarException;
import com.diefesson.difcomp.parser.Action;
import com.diefesson.difcomp.parser.SLRKey;
import com.diefesson.difcomp.parser.SLRTable;
import com.diefesson.difcomp.rustlike.RLTokens;

public class RustLikeTest {

    @Test
    public void uniqueTokenIds() {
        RLTokens[] tts = RLTokens.values();
        for (int i = 0; i < tts.length; i++) {
            for (int j = i + 1; j < tts.length; j++) {
                RLTokens tti = tts[i];
                RLTokens ttj = tts[j];
                assertNotEquals(tti.id, ttj.id,
                        "%s and %s have conflicting ids".formatted(tti, ttj));
            }
        }
    }

    @Test
    public void grammarBuild() throws GrammarException {
        assertAll(() -> rlGrammar());
    }

    @Test
    public void noAmbiguity() throws GrammarException {
        SLRTable table = SLRTable.compute(rlGrammar());
        for (SLRKey key : table.keys()) {
            List<Action> actions = table.getList(key);
            assertEquals(1, actions.size(),
                    "Conflict: " + actions.stream().map(Object::toString).collect(joining(", ")));
        }
    }
}

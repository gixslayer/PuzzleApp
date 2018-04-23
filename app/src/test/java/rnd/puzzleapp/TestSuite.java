package rnd.puzzleapp;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import rnd.puzzleapp.puzzle.BridgeTest;
import rnd.puzzleapp.puzzle.SpanTest;

@RunWith(Suite.class)

@Suite.SuiteClasses({
        SpanTest.class,
        BridgeTest.class
})

public class TestSuite {
}

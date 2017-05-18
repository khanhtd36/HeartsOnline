package controller;

import javafx.animation.RotateTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.Node;
import javafx.util.Duration;

import java.util.ArrayList;

public class AnimationUtil {
    public static void cardMoveToWest(Node... cards) {
        ArrayList<ScaleTransition> scaleTransitions = new ArrayList<>();
        ArrayList<TranslateTransition> translateTransitions = new ArrayList<>();
        ArrayList<RotateTransition> rotateTransitions = new ArrayList<>();
        for(Node node : cards) {
            scaleTransitions.add(new ScaleTransition(new Duration(700), node));
            translateTransitions.add(new TranslateTransition(new Duration(700), node));
            rotateTransitions.add(new RotateTransition(new Duration(700), node));
        }
    }

    public static void cardMoveToNorth(Node... cards) {

    }

    public static void cardMoveToEast(Node... cards) {

    }

    public static void cardMoveToMe(Node... cards) {

    }

    public static void cardDeskAppear(Node[] west, Node[] north, Node[] east, Node[] me) {
        
    }

    public static void leftArrowAnnimate(Node leftArrow) {

    }

    public static void rightArrowAnnimate(Node rightArrow) {

    }

    public static void upArrowAnnimate(Node upArrow) {

    }
}

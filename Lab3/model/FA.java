package model;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class FA {
    private List<Transition> transitions;
    private List<String> states;
    private List<String> alphabet;
    private String initialState;
    private List<String> outputStates;

    public FA(String filename) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(filename));
            reader.lines().forEach(line -> {

                Pattern pattern = Pattern.compile("^([a-z_]*)=");
                Matcher matcher = pattern.matcher(line);
                if (matcher.find()) {
                    String key = matcher.group(1);
                    String value = line.substring(matcher.end()).replaceAll("[{}()]", "");
                    switch (key) {
                        case "states" -> states = Stream.of(value.split(",")).map(String::strip).collect(Collectors.toList());
                        case "alphabet" -> alphabet = Stream.of(value.split(",")).map(String::strip).collect(Collectors.toList());
                        case "initial_state" -> initialState = value;
                        case "final_states" -> outputStates = Stream.of(value.split(",")).map(String::strip).collect(Collectors.toList());
                        case "transitions" -> {
                            String[] transitionString = value.split(";");
                            transitions = new ArrayList<>();
                            for (String s : transitionString) {
                                String[] transitionParts = s.split(",");
                                transitions.add(new Transition(transitionParts[0].strip(), transitionParts[1].strip(), transitionParts[2].strip()));
                            }
                        }
                    }
                }
            });
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }


    private List<String> stringToList(String string) {
        return List.of(string.split(""));
    }

    private String listToString(List<String> list) {
        return String.join("", list);
    }

    private void printListOfString(String listName, List<String> list) {
        System.out.print(listName + "={");
        IntStream.range(0, list.size()).forEach(i -> {
            System.out.print(list.get(i));
            if (i != list.size() - 1) System.out.print(",");
        });
        System.out.println("}");
    }

    public void printStates() {
        printListOfString("states", states);
    }

    public void printAlphabet() {
        printListOfString("alphabet", alphabet);
    }

    public void printInitialState() {
        System.out.println("initialState=" + initialState);
    }

    public void printOutputStates() {
        printListOfString("outputStates", outputStates);
    }

    public void printTransitions() {
        System.out.println("transitions={");
        transitions.forEach(System.out::println);
        System.out.println("}");
    }

    public boolean checkAccepted(List<String> word) {
        String currentState = initialState;
        int index = 0;
        int last = word.size();
        for (String letter : word) {
            index += 1;
            boolean found = false;
            for (Transition transition : transitions) {
                if (transition.getT1().equals(currentState) && transition.getT2().equals(letter))
                    if ((!outputStates.contains(transition.getRes()) && index!=last) || (outputStates.contains(transition.getRes()) && index==last))
                     {
                    currentState = transition.getRes();
                    found = true;
                    System.out.println(index);
                    System.out.println(transition.toString());
                    break;
                }
            }
            if (!found) return false;
        }
        for (String outputState : outputStates) {
            if (outputState.equals(currentState)) return true;
        }
        return false;
    }

    public boolean checkAccepted(String word) {
        //System.out.println(this.stringToList(word));
        return checkAccepted(this.stringToList(word));
    }

}
package com.slimanice.distributedgeneticalgorthm;

import com.slimanice.distributedgeneticalgorthm.containers.SimpleContainer;
import com.slimanice.distributedgeneticalgorthm.containers.MainContainer;

public class ConsoleApplication {
    public static void main(String[] args) {
        // Start the master container
        MainContainer.main(args);
        // Start the island container
        SimpleContainer.main(args);
    }
}

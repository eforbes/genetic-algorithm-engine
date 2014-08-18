A Simple Genetic Algorithm for the Geometric Connected Dominating Set Problem
=============================

A simple [genetic algorithm](http://en.wikipedia.org/wiki/Genetic_algorithm) engine for the geometric connected dominating set problem written in Java.


![](http://i.imgur.com/XuGpwUE.png)  
[More screenshots](http://imgur.com/a/bunTi)

Abstract
-----------------------------
For this project, I created a custom genetic algorithm engine from scratch in Java and used it to collect data and compare results from various combinations of standard genetic algorithm operators for selection, crossover, and mutation including the N4N hypermutation proposed by Alkhalifah and Wainwright[2]. These results were also compared with results from the simulated annealing and foolish hill climbing algorithms, with various perturbation functions. 

Introduction
-----------------------------
The geometric connected dominating set problem (GCDSP) is formally defined as follows: Given a set of points in a plane, P, and a constant B, find the minimum |P'| such that all points P-P' are within euclidean distance of B of some point in P' and such that the graph G=(P^',E) with an edge between two points in P' if and only if they are within distance B of each other is connected[1]. 

This problem is also informally refered to as the “radar” problem and can be stated as follows: given a set of cities, we need to select the minumum number of cities in which to install a range limited radar transmitter such that all cities receive radar coverage and all transmitters are within range of another transmitter. 

An example dataset and solution is shown in Figure 1. Each point represents a city. Square points are considered “selected” and will contain a transmitter whose range is represented by the surrounding circle. In order to be a feasible solution, all points must be within a circle and all selected points must have a path to all other selected points. 

Full report
-----------------------------
View the full report in the repository: "Forbes SGA Report.pdf"

Contact
-----------------------------
forbes.evan@gmail.com

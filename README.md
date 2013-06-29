agal
====

Another Genetic Algorithm Library (AGAL)

Authors: David Schmidt (dschmidt13@gmail.com)


TODO: actual readme stuff (installation, usage, dependencies, etc)


Introduction
============
This project's aim is to provide a Genetic Algorithm library which is:
-accessible (easily used by GA newbies and researchers alike)
-extensible (easily extended for ANY custom evolutionary application)
-scalable (translucently multithread and/or used with distributed hardware)
-tunable (can optimize searches by monitoring evolution and making decisions during evolution)
-well-written and thoroughly documented
-pretty small (without a sprawling, unfocused architecture or scores of trivial classes)



A (Very) Brief Introduction to Genetic Algorithms
=================================================
TODO (if you're reading this in the early stages, chances are you already know enough about the subject to scrape by)



A New Theory of Distributed Evolution
=====================================
DISCLAIMER: I have no rigorous mathematical proofs to support this theory. However, I do have strong mathematical intuition.

Judging by the feature sets of other frameworks, two popular schools of thought (at time of writing) on how to best use resources in a distributed computing environment is to either (1) run the evolution multiple times in small single-machine "islands," and periodically "migrate" solutions between islands to ensure diversity; or (2) run the evolution as usual on a master machine, and distribute the often time-consuming task of fitness evaluation (via simulations or the like) to the rest of the machines in the network.

TODO

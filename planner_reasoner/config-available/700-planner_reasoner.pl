/*
Copyright 2017 Ericsson AB

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

  http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

:- module('700-planner_reasoner', []).

/** <module> Planner Reasoner
*/

:- use_module(library(semweb/rdf_library)).
:- rdf_attach_library(planner_reasoner(rdf)).

:- rdf_load_library(pddl).
:- rdf_load_library(planner).
:- rdf_load_library(pddl_example).
:- rdf_load_library(warehouse_domain).
:- rdf_load_library(warehouse_problem).

:- use_module(library(pddl_generator)).
:- use_module(library(metric_ff)).

:- use_module(planner_reasoner(applications/planner_reasoner_server), []).

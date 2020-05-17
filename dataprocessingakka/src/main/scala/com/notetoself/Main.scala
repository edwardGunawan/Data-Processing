package com.notetoself

import akka.actor.{ActorSystem, Props}

/*
  Data Processing for Logs, Aggregate all the Http Status in the web log corresponding to the List of Logs.
    return : Map[HttpStatus, List[Logs]]

  Main will send signal to supervisor.
  Supervisor will start by sending signal to ingestion.
  Ingestion responsibility:
    - Get value from data store
    - Initialized Master Node
    - Filter valid IP
    - Send All the data to Master node
  Master Node:
    - Initialized Worker Node
    - Send execute result to Worker node to convert it to Logs
    - Aggregate the result from Worker node from
    - Send the result back to Ingestion Actor
   Worker Node:
    - Transform String to Logs
    - Send result to Master
 */
object Main extends App {
  val system = ActorSystem("DataProcessingAkka")
  val supervisorActor = system.actorOf(Supervisor.props(100), "supervisor")

  supervisorActor ! Supervisor.Start

}

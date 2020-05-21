@start[atomic]
+activate
  <- lookupArtifact("mainUI", MainUI);
     !!notifyPresence.

+!notifyPresence
  <- .wait(5000);
     .my_name(Name);
     writeOnConsole(Name, "I'm Alive!");
     !!notifyPresence.
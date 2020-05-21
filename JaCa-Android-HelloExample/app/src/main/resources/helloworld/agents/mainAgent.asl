!init.

+!init
	<-  makeArtifact("mainUI", "it.unibo.pslab.jaca_android.helloexample.MainUI",[],MainUI);
    	focus(MainUI).

+ui_ready [artifact_name(Id,MainUI)]
    <- println("MainUI ready.");
       .send(otherAgt, tell, activate).

+new_ui_input("")
    <- println("do nothing").

+new_ui_input(Text)
    <- .my_name(Name);
       .concat("Observed: ", Text, Str);
       writeOnConsole(Name, Str).
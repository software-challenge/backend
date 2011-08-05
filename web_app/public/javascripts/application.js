    function clearFlash(){
      $("#flash").remove()
    }

    function addFlash(type, message){
      if($("#flash").length == 0){
        $("#content").prepend('<div id="flash" style="margin: 0 -10px;"></div>')
      }

      if(type == "notice"){
        $("#flash").append('<div class="ui-widget"><div class="ui-state-highlight ui-corner-all" style="margin: 0.6em 0; padding: 0 .7em;"><p><span class="ui-icon ui-icon-info" style="float: left; margin-right: .3em;"></span><strong>Hinweis:</strong>'+message+'</p></div></div>')
      }else{
        $("#flash").append('<div class="ui-widget"><div class="ui-state-error ui-corner-all" style="margin: 0.6em 0; padding: 0 .7em;"><p><span class="ui-icon ui-icon-alert" style="float: left; margin-right: .3em;"></span><strong>Fehler:</strong> '+message+'</p></div></div>')
      }
    }




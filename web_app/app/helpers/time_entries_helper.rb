module TimeEntriesHelper
  def minutes_for_input(minutes)
    hs = 0;
    ms= 0;
    if minutes >= 60
      hs = minutes / 60
      ms = minutes - hs*60
    else minutes > 0
      ms = minutes
    end
    "#{hs < 100 ? "0"*(2-hs.to_s.length) : ""}#{hs}:#{"0"*(2-ms.to_s.length)}#{ms}"
  end
end

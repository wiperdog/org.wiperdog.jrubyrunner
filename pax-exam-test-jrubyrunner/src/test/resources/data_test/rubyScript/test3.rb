
require 'os'
os = ""
if(OS.windows?)
    os = "windows"
elsif
  if(OS.linux?)
    os = "linux"
  else
    os = "other" 
  end  
end

#script running return os type
return os

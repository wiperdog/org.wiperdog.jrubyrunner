puts "Variable a is : #{a}"
puts "Variable b is : #{b}"
puts "Variable c is : #{c}"

returnData = ""
c.each do |e|
  returnData += e
  returnData += " "
end
#script will be return string "Hello JrubyRunner"
return returnData.strip
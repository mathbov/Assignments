sorter: allocate.o  fileHandling.o  parsing.o  utils.o  sorter.o
	ld -o  sorter  allocate.o  fileHandling.o parsing.o  utils.o  sorter.o
allocate.o: allocate.s
	as  --gstabs  -o  allocate.o  allocate.s

fileHandling.o: fileHandling.s
	as  --gstabs  -o  fileHandling.o  fileHandling.s

parsing.o: parsing.s
	as  --gstabs  -o  parsing.o  parsing.s

utils.o: utils.s
	as  --gstabs  -o  utils.o  utils.s

sorter.o: sorter.s
	as  --gstabs  -o  sorter.o  sorter.s

.PHONY: clean
clean:
	rm -f *.o  sorter

.section .data
.section .text
.globl _start

_start:
	movq 16(%rsp), %r15	#Offset rsp by 16 to find first argument - the file name
	call openFile
	call fileSize
	call allocateForInputFile
	call readFile
	call convertToNumbers
	call insertionSort
	movq $0, %r8		# Used for which number we are printing
	movq $0, %r9		# Used to check if we need a newline
	call printNumLoop
	call closeFile
	call exit
	
insertionSort:
	push %rbp
	movq %rsp, %rbp
	push %rax	#Save registers
	push %rdi
	push %rsi
	push %rdx
	push %rcx
	push %r8
	push %r9
	push %r10
	push %r11
	push %r12
	push %r13
	push %r14
	push %r15
	movq $0, %r15		#Counter for 1st number
	movq $2, %r14		#Counter for 2nd number
	movq %r12, %rax
	movq $0, %rdx
	movq $8, %rcx
	cqto
	idivq %rcx
	movq %rax, %r12	#Number of coordinates
	movq $2, %r13		 #Index
	jmp sorter
	
sorter:
	cmp %r12, %r14			#Compares 2nd number index with total numbers
	jg insertionExit		#If index greater than we are done
	movq 8(%r11, %r15, 8), %rdi	#Takes the 1st number Y
	movq 8(%r11, %r14, 8), %rsi	#2nd Y
	movq (%r11, %r15, 8), %rdx	#X of 1st
	movq (%r11, %r14, 8), %rcx	#X of 2nd
	cmp %rdi, %rsi			#Compare numbers
	jge movePointersUp		#Everything is in order
	jmp swap			#Otherwise we need to swap

movePointersUp:
	addq $2, %r13 		#Index of what number we now need to sort
	movq %r13, %r15	
	subq $2, %r15		#Indexing 1st number
	movq %r13, %r14	#Indexing 2nd number
	jmp sorter

movePointersDown:
	cmp $0, %r15		#Check if we are at the 1st number of the file
	je index0		
	subq $2, %r15		#Move pointers
	subq $2, %r14
	jmp sorter

index0:
	addq $2, %r13		#Raise index
	movq %r13, %r15	
	subq $2, %r15		#Indexing 1st
	movq %r13, %r14	#2nd
	jmp sorter                    


swap:               
	push %rbp
	movq %rsp, %rbp
	push %rax	#Save registers
	push %rdi
	push %rsi
	push %rdx
	push %rcx
	push %r8
	push %r9
	push %r10
	push %r11
	push %r12
	push %r13
	push %r14
	push %r15
	
	imulq $8, %r15 	#1st number index
	addq $8, %r15		#Y coordinate
	addq %r11, %r15	#Adds the memory pointer
	imulq $8, %r14		#2nd number index
	addq $8, %r14		#Y coordinate
	addq %r11, %r14	#Adds the memory pointer
	movq %rdi, (%r14)	#Swapping Y 
	movq %rsi, (%r15)
	subq $8, %r15		#X coordinate
	subq $8, %r14		#X coordinate
	movq %rdx, (%r14)	#Swapping X
	movq %rcx, (%r15)
	
	pop %r15
	pop %r14
	pop %r13
	pop %r12
	pop %r11
	pop %r10
	pop %r9	 #Restore registers
	pop %r8
	pop %rcx
	pop %rdx
	pop %rsi
	pop %rdi
	pop %rax
	movq %rbp, %rsp
	pop %rbp
	jmp movePointersDown

insertionExit:
	pop %r15
	pop %r14
	pop %r13
	pop %r12
	pop %r11
	pop %r10
	pop %r9	 #Restore registers
	pop %r8
	pop %rcx
	pop %rdx
	pop %rsi
	pop %rdi
	pop %rax
	movq %rbp, %rsp
	pop %rbp
	ret
	
	
printNewline:
	push $10 
	movq %rsp, %rsi
	movq $1, %rdx
	movq $1, %rax
	movq $1, %rdi
	push %r8
	push %r11
	syscall
	pop %r11
	pop %r8
	pop %rax
	jmp printNumLoop
	
printExit:
	ret	
printNumLoop:
	cmp %r8, %r12
	je printExit
	movq (%r11, %r8), %rdi 	# moves 8 bytes to rdi 
	push %r8
	push %r11
	call printNum
	pop %r11
	pop %r8
	addq $8, %r8
	addq $1, %r9
	movq %r9, %rax
	movq $0, %rdx
	movq $2, %rcx
	cqto
	idivq %rcx
	cmp $0, %rdx
	je printNewline
	jmp printNumLoop

openFile:	
	movq $2, %rax
	movq %r15, %rdi
	movq $0, %rsi
	syscall
	movq %rax, %r15 	#File descriptor in r15
	ret
fileSize:
	movq %r15, %rdi
	call getFileSize
	movq %rax, %r14	#Filesize in r14
	ret
allocateForInputFile:
	movq %r14, %rdi
	call allocate
	movq %rax, %r13	#Buffer in r13
	ret
readFile:
	movq $0, %rax  	#0 for read syscall
	movq %r15, %rdi	#0 for stdin(file descriptor)
	leaq (%r13), %rsi	#adress (pointer) to input buffer
	movq %r14, %rdx	#rdx is size of buffer
	syscall
	ret
	
closeFile:	
	movq $3, %rax		#Close file
	movq %r15, %rdi	#File descriptor
	syscall
	ret
	
convertToNumbers:
	leaq (%r13), %rdi
	movq %r14, %rsi
	call getLineCount
	movq %rax, %r12	#Number of pairs
	imulq $16, %r12
	movq %r12, %rdi
	call allocate
	leaq (%rax), %r11	#Buffer for numbers in r11
	leaq (%rax), %rdx	#Buffer to parse numbers to
	leaq (%r13), %rdi	#Buffer for data to parse
	movq %r14, %rsi	#Size of data
	call parseData
	ret

exit:
	movq $60, %rax            # rax: int syscall number
	movq $0, %rdi             # rdi: int error code
	syscall

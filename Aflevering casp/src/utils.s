# Print RDI as an unsigned integer following by a newline.
# Note: the function does not follow the ordinary calling convention,
#       but restores all registers.
.type printNum, @function
.globl printNum
printNum:
	push %rbp
	movq %rsp, %rbp

	# save
	push %rax
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

	movq %rdi, %rax # arg

	movq $1, %r9 # we always print "\n"
#	push $10 # '\n'
	push $32 # A space
.LprintNum_convertLoop:
	movq $0, %rdx
	movq $10, %rcx
	idivq %rcx
	addq $48, %rdx # '0' is 48
	push %rdx
	addq $1, %r9
	cmpq $0, %rax   
	jne .LprintNum_convertLoop
.LprintNum_printLoop:
	movq $1, %rax # sys_write
	movq $1, %rdi # stdout
	movq %rsp, %rsi # buf
	movq $1, %rdx # len
	syscall
	addq $8, %rsp
	addq $-1, %r9
	jne .LprintNum_printLoop

	# restore
	pop %r15
	pop %r14
	pop %r13
	pop %r12
	pop %r11
	pop %r10
	pop %r9
	pop %r8
	pop %rcx
	pop %rdx
	pop %rsi
	pop %rdi
	pop %rax

	movq %rbp, %rsp
	pop %rbp
	ret

<h1 align="center">🌹 Poppy 🌹</h1>

Poppy is a stack-based bytecode interpreter in Kotlin that I made for funsies and for learning purposes, and because programming is *fun*. :3

The reason why I made this is because I'm developing [Butterscotch](https://github.com/MrPowerGamerBR/Butterscotch), a GameMaker: Studio runner. GameMaker: Studio games use a stack-based bytecode for their code, so, in Butterscotch, we have a bytecode interpreter.

However, Butterscotch was created with the help of LLMs, so while I know how the interpreter works on a high level and while I have reviewed the interpreter code before, I didn't *fully* understand how it actually worked ("uuhh what the stack actually does???"). So, to keep my programming skills in check, I've decided to create my own stack-based bytecode interpreter. I've already written a [stack-based bytecode interpreter by hand years ago](https://mrpowergamerbr.com/br/blog/2020-09-20-inutilidades-gamemaker-studio-vm), but because it was so long ago, I've decided to do it again!

This is a VERY basic bytecode interpreter, done in 2h30m, so it has a lot of missing features (only integer add/sub/mul/div/mod support, can only push integers and string constants, etc). But it works! The purpose of the project was not to be a full-featured bytecode interpreter, it was to be a "featured enough" bytecode interpreter that I could test my knowledge and fill in gaps. After all, programming is a skill, and if you don't "train" it by coding things manually, you will get lazy.

The bytecode itself is very simple: Each instruction has an int32 opcode and an int32 extra field.

Here's a simple "multiply two numbers and print the result" example:

```
[println]
[]
// Pushes 8 to the stack, the stack is [8]
push_int 8
// Pushes 8 to the stack, the stack is [8, 8]
push_int 8
// Pops the top 2 elements from the stack, multiplies it and pushes - The stack is [64]
mul
// Calls the function name with index 0 (in this case, println) and pops and pushes 1 argument to the function
call 0, 1
```

Here's a FizzBuzz (`fizzbuzz.poppy`) example:

```
[print, println]
["Fizz", "Buzz", "<<iteration end>>"]
// Given an integer n, for every positive integer i <= n, the task is to print,
// "FizzBuzz" if i is divisible by 3 and 5,
// "Fizz" if i is divisible by 3,
// "Buzz" if i is divisible by 5
// "i" as a string, if none of the conditions are true
// ---
// For reference, math order is:
// a = pop()
// b = pop()
// b % a
// So for a stack like this: [5, 3]
// The order would be
// 5 % 3
//
// This is "n"
push_int 64
push_int 1
// We do +1 because the check is >= NOT >
add
store 0
// This is "i"
// We start at 1
push_int 1
store 1
// Now we need to create a "loop" that repeats from n to i
// This will load the "n"
load 0
load 1
// This will push "true" or "false"
equal
// If it is false, we'll exit because we have already done what we needed to do
// sf = "Skip if false"
sf 8
bye

// This is a "have we printed Fizz or Buzz on this iteration?" flag
push_int 0
store 2

// Load the i (div 3)
load 1
push_int 3
mod
push_int 0
equal
sf 32
// It is divisible by 3!
push_const 0
call 0, 1
// Set the printed flag
push_int 1
store 2

// Load the i (div 5)
load 1
push_int 5
mod
push_int 0
equal
sf 32
// It is divisible by 5!
push_const 1
call 0, 1
// Set the printed flag
push_int 1
store 2

// Did we print this iteration?
load 2
st 16
// If not, print i
load 1
call 0, 1

// And now we need to load the i AGAIN
load 1
// And +1!
push_int 1
add
// And store it!
store 1

// Before we leave, we'll print the "<<iteration end>>" string
// Poppy does not support the "\n" constant because I'm lazy
// But we COULD abuse the fact that the interpreter supports println to print "nothing"
// However we'll keep the "<<iteration end>>" string because it is useful to trace the prints
push_const 2
call 1, 1
// call 1, 0

// Jump back to the top of the loop
jump 48
```

Because Poppy is a bytecode interpreter, we need to assemble the code using `java -jar poppy.jar assemble PoppyFile.poppy` before executing it. This generates a binary `.pxppy` file.

Then, you can execute it with `java -jar poppy.jar execute PoppyFile.pxppy`.

```
Chunk: FUNC (14 bytes)
Chunk: STRG (28 bytes)
Chunk: CODE (352 bytes)
Chunks: {FUNC=[B@1753acfe, STRG=[B@7c16905e, CODE=[B@2a2d45ba}
1<<iteration end>>
2<<iteration end>>
Fizz<<iteration end>>
4<<iteration end>>
Buzz<<iteration end>>
Fizz<<iteration end>>
7<<iteration end>>
8<<iteration end>>
Fizz<<iteration end>>
Buzz<<iteration end>>
11<<iteration end>>
Fizz<<iteration end>>
13<<iteration end>>
14<<iteration end>>
FizzBuzz<<iteration end>>
16<<iteration end>>
17<<iteration end>>
Fizz<<iteration end>>
19<<iteration end>>
Buzz<<iteration end>>
Fizz<<iteration end>>
22<<iteration end>>
23<<iteration end>>
Fizz<<iteration end>>
Buzz<<iteration end>>
26<<iteration end>>
Fizz<<iteration end>>
28<<iteration end>>
29<<iteration end>>
FizzBuzz<<iteration end>>
31<<iteration end>>
32<<iteration end>>
Fizz<<iteration end>>
34<<iteration end>>
Buzz<<iteration end>>
Fizz<<iteration end>>
37<<iteration end>>
38<<iteration end>>
Fizz<<iteration end>>
Buzz<<iteration end>>
41<<iteration end>>
Fizz<<iteration end>>
43<<iteration end>>
44<<iteration end>>
FizzBuzz<<iteration end>>
46<<iteration end>>
47<<iteration end>>
Fizz<<iteration end>>
49<<iteration end>>
Buzz<<iteration end>>
Fizz<<iteration end>>
52<<iteration end>>
53<<iteration end>>
Fizz<<iteration end>>
Buzz<<iteration end>>
56<<iteration end>>
Fizz<<iteration end>>
58<<iteration end>>
59<<iteration end>>
FizzBuzz<<iteration end>>
61<<iteration end>>
62<<iteration end>>
Fizz<<iteration end>>
64<<iteration end>>
```
# Huffman Coding – Divide and Conquer & Greedy Algorithms Project
**Course:** Design and Analysis of Algorithms (CSCI 6221)  
**Instructor:** Professor Aurora  
**Student ID Option:** 9 – Huffman Coding  
**Language:** Java  
**Author:** Parviz Khairullaev  
**Date:** 10/15/2025

---

## 📘 Project Overview
This project implements **Huffman Coding**, a classic *Greedy Algorithm* that assigns optimal prefix-free binary codes to a set of symbols based on their frequencies.  

The algorithm ensures:
1. No code is the prefix of another (prefix-free property).  
2. The total weighted code length (frequency × code length) is minimized.  

This satisfies the project specification:

> **Option 9:**  
> Given a set of symbols and their frequency of usage, find a binary code for each symbol such that:  
> (a) Binary code for any symbol is not the prefix of another.  
> (b) The weighted length of codes for all symbols (weighted by their usage frequency) is minimized.

---

## 🧩 Implementation Summary
The implementation follows the standard **O(n log n)** Huffman algorithm using a Min-Priority Queue.

- **Phase 1:** Construct a min-heap (priority queue) from all input symbols and their frequencies.  
- **Phase 2:** Iteratively extract the two smallest nodes, merge them, and reinsert their combined frequency until one root remains.  
- **Phase 3:** Recursively traverse the final tree to assign binary codes (0 for left, 1 for right).

The implementation automatically satisfies both prefix-free and minimum-weight properties through the greedy merge process.

---

## 🧠 Theoretical Basis
The pseudocode structure is adapted from the algorithmic description in the *Wikipedia article on Huffman Coding* (as recommended by Professor Aurora in Lecture 5 slides):
> [https://en.wikipedia.org/wiki/Huffman_coding](https://en.wikipedia.org/wiki/Huffman_coding)

Time complexity was analyzed theoretically as:
\[
T(n) = O(n \log n)
\]
due to repeated `extractMin()` and `insert()` heap operations on \( n \) symbols.

---

## 🖥️ How to Compile and Run

### **Option 1: Using Command Line**
1. **Navigate to the project folder:**
   ```bash
   cd HuffmanCodingProject
2. Compile the code:
   
   javac HuffmanCoding.java
   
3. Run the program: 

  java HuffmanCoding




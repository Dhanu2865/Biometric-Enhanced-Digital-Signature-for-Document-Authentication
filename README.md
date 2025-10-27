# Biometric Enhanced Digital Signature for Document Authentication

## Overview

This project implements a **Biometric Enhanced Digital Signature (BEDS)** system using Java. It combines digital signatures with biometric verification (fingerprints) to ensure that a document is signed by the **genuine user**. The system enhances security beyond traditional digital signatures by binding the signature to a biometric key derived from a fingerprint image.

---

## Features

- **Key Generation:** Uses Elliptic Curve Digital Signature Algorithm (ECDSA) to generate public-private key pairs.  
- **Biometric Binding:** Encrypts the private key with a key derived from a fingerprint image, ensuring that only the correct user can sign.  
- **Document Signing:** Supports signing any text (`.txt`) document with biometric-enhanced digital signatures.  
- **Receiver Verification:** The receiver can verify the authenticity and integrity of the document using the sender's public key.  
- **Intermediate Debugging:** Prints detailed intermediate steps, including fingerprint hash, document content, and partial signature for easy monitoring.  
- **Socket Communication:** Sender and receiver communicate over TCP sockets for real-time document transfer.  

---

## Technologies Used

- **Java** for application logic  
- **AES** for encrypting the private key  
- **ECDSA** for digital signatures  
- **Fuzzy hashing** of fingerprint images to generate AES keys  
- **Socket Programming** for sender-receiver communication  
- **File I/O** for storing documents, keys, and signatures  

---
## Project Structure

<img width="841" height="223" alt="image" src="https://github.com/user-attachments/assets/66e6e1d6-7ce8-41ba-9f3f-acda7a754656" /># Biometric Enhanced Digital Signature for Document Authentication


---

## How It Works

### Sender Side:

1. Generates an EC keypair if it doesn’t exist.  
2. Loads fingerprint image and generates a biometric key.  
3. Encrypts the private key with the biometric key.  
4. Signs the document using the decrypted private key.  
5. Sends the document, signature, and public key to the receiver via socket.  

### Receiver Side:

1. Receives the document, signature, and public key.  
2. Verifies the signature using the public key.  
3. Stores the document, signature, and verification result.  
4. Prints verification status and intermediate details in the terminal.  

---

## Future Improvements

- Support for **PDF files** in addition to text documents.  
- A **GUI** for uploading documents and fingerprint images.  
- Multi-user support with secure storage of multiple fingerprints.  
- Integration with **online storage or cloud** for document signing and verification.  

---

## Usage
  - description: "Clone the repository"
   - code: |
      git clone https://github.com/username/crypto_project.git

  - description: "Navigate to the project folder"
   - code: |
      cd crypto_project

  - description: "Compile all Java files"
   - code: |
      javac utils/*.java sender/*.java receiver/*.java

  - description: "Run the receiver"
   - code: |
      java receiver.Receiver

  - description: "Run the sender"
   - code: |
      java sender.Sender

  - description: "Monitor the terminal for intermediate results and signature verification"
   - code: |
      # Watch the console output

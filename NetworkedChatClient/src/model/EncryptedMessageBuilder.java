/* File: ClientController.java
 *
 * Authors:
 *     Alex Viznytsya
 *     Sean Martinelli
 *
 *
 * Date:
 *     12/07/2017
 *
 * Class description:
 *     This class is responsible for building RSA public and private
 *     keys and also encrypting and decrypting messages.
 *
 * Source used for prime number algorithm:
 *     http://www.stoimen.com/blog/2012/05/08/
 *     computer-algorithms-determine-if-a-number-is-prime/
 */

package model;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class EncryptedMessageBuilder
{
    private static EncryptedMessageBuilder encryptedMsgBuilderInstance;

    private BigInteger e, d;
    private BigInteger p, q;
    private BigInteger phi, n;
    private int blockSize = 8;

    //Constructor is private to implement Singleton Pattern
    private EncryptedMessageBuilder()
    {
        encryptedMsgBuilderInstance = null;
    }

    //
    // Returns the current instance of EncryptedMessageBuilder.
    // If an instance has not been instantiated yet, a new instance is created.
    //
    public static EncryptedMessageBuilder getInstance()
    {
        //Check if an instance has already been created.
        if(encryptedMsgBuilderInstance == null)
        {
            synchronized(EncryptedMessageBuilder.class)
            {
                if(encryptedMsgBuilderInstance == null)
                    return encryptedMsgBuilderInstance = new EncryptedMessageBuilder();
                else
                    return encryptedMsgBuilderInstance;
            }
        }
        else
        {
            return encryptedMsgBuilderInstance;
        }
    }

    //
    // Encrypt a message using the RSA algorithm.  The message is first broken down into
    // blocks and then each block is encrypted separately.  The blocks are added to an
    // ArrayList and then returned.
    //
    public ArrayList<BigInteger> encryptMessage(String message, PublicRSAKey publicKeyToUse)
    {
        ArrayList<BigInteger> encryptedMessage = new ArrayList<>();

        //Get n and e values
        BigInteger n = publicKeyToUse.getN();
        BigInteger e = publicKeyToUse.getE();

        //Loop through the entire message
        for(int i = 0; i < message.length(); i += blockSize)
        {
            BigInteger bigTotal = BigInteger.valueOf(0);

            //Break the message down into blocks
            for(int j = 0; j < blockSize; ++j)
            {
                //Convert the current block into an integer value
                if((i+j) < message.length())
                {
                    BigInteger currentChar = BigInteger.valueOf((int) message.charAt(i + j));
                    BigInteger charTotal = BigInteger.valueOf(128);
                    charTotal = charTotal.pow(j);
                    charTotal = charTotal.multiply(currentChar);

                    bigTotal = bigTotal.add(charTotal);
                }
            }

            //Encrypt the calculated integer value for the block and add it to the ArrayList
            bigTotal = bigTotal.modPow(e, n);
            encryptedMessage.add(bigTotal);
        }

        return encryptedMessage;
    }

    //
    // Decrypt a message previously encrypted by the encryptMessage method.
    // The decrypted message is returned as a string.
    //
    public String decryptMessage(ArrayList<BigInteger> encryptedMessage)
    {
        StringBuilder message = new StringBuilder();

        //Loop through each block in the encrypted message
        for(BigInteger block : encryptedMessage)
        {
            StringBuilder chunk = new StringBuilder();

            //Decrypt the block integer value
            block = block.modPow(d, n);

            //Extract each char from the decrypted integer block value
            for(int i = blockSize-1; i >= 0; --i)
            {
                BigInteger position = BigInteger.valueOf(128);
                position = position.pow(i);

                char letter = (char) block.divide(position).intValue();

                block = block.mod(position);
                chunk.append(letter);
            }

            //Reverse the current chunk of decrypted chars and add them to the message
            chunk.reverse();
            message.append(chunk);
        }

        return message.toString();
    }

    //
    // Generates and returns the current public key.
    //
    public PublicRSAKey getPublicKey()
    {
        return new PublicRSAKey(n, e);
    }

    //
    // Update P and Q and regenerate the RSA Values.
    //
    public int setPQValues(long p, long q)
    {
        //Make sure q is prime
        if(!isPrime(q))
            return -2;

        //Make sure p is prime
        if(!isPrime(p))
            return -1;

        //Make sure the product of p and q are large enough
        if(!primesAreLargeEnough(p, q))
            return 0;

        //Update p and q and regenerate the RSA values
        this.p = BigInteger.valueOf(p);
        this.q = BigInteger.valueOf(q);
        generate_RSA_Values();

        return 1; //Success
    }

    //
    // Generates n, phi, e, and d that are needed for the RSA algorithm.
    //
    private void generate_RSA_Values()
    {
        //Calculate N and Phi
        n = p.multiply(q);
        calculatePhi();

        //Calculate e and d
        calculateE();
        calculateD();
    }

    //
    // Returns true if the supplied number is prime,
    // false if not.
    // The source for this algorithm is:
    // http://www.stoimen.com/blog/2012/05/08/computer-algorithms-determine-if-a-number-is-prime/
    //
    private boolean isPrime(long numToCheck)
    {
        long currentNum = 2;

        //Set the upper limit as the square root of numToCheck
        double upperLimit = Math.sqrt(numToCheck);

        //Check every number up to the upper limit to
        //see if numToCheck is divisible by it
        while (currentNum <= upperLimit)
        {
            if((numToCheck % currentNum) == 0)
                return false;
            ++currentNum;
        }

        return true;
    }

    //
    // Returns true if the two prime numbers are large enough to be used for
    // the current block size, false if not.
    //
    private boolean primesAreLargeEnough(long num1, long num2)
    {
        //Calculate lower limit for (num1 * num2)
        BigInteger lowerLimit = BigInteger.valueOf(128);
        lowerLimit = lowerLimit.pow(blockSize);

        //Calculate (num1 * num2)
        BigInteger num1_BigInt = BigInteger.valueOf(num1);
        BigInteger num2_BigInt = BigInteger.valueOf(num2);
        BigInteger multResult = num1_BigInt.multiply(num2_BigInt);

        //Test if the result of the multiplication is >= to the lower limit
        return (multResult.compareTo(lowerLimit) >= 0);
    }

    //
    // Select random values to be used for p and q from the resource file.
    //
    public void generate_PQ_Values() throws TimeoutException, FileNotFoundException
    {
        //Open file with prime numbers
        Scanner resourceFile = new Scanner(
                new File("./resources/primeNumbers.rsc"));

        ArrayList<String> primeNumbers = readPrimesFromFile(resourceFile);

        selectPQValues(primeNumbers);

        generate_RSA_Values();
    }

    //
    // Select two prime numbers from an array list of prime numbers.
    // The product of the two prime numbers are validated to make sure
    // their product is greater than 128^blocksize.
    //
    private void selectPQValues(ArrayList<String> primeNumbers) throws TimeoutException
    {
        int numAttempts = 0;

        Random randomNumber = new Random();
        BigInteger pqProduct;

        //Calculate lower bound for the product of p and q
        BigInteger numCharValues = BigInteger.valueOf(128);
        BigInteger lowerBound = numCharValues.pow(blockSize);

        //Keep choosing p and q values until product exceeds lower bound
        do {
            int indexOne = randomNumber.nextInt(primeNumbers.size());
            int indexTwo = randomNumber.nextInt(primeNumbers.size());

            //Assign p and q
            p = new BigInteger(primeNumbers.get(indexOne));
            q = new BigInteger(primeNumbers.get(indexTwo));

            // If either p or q is not prime or they are the same
            // force the loop to repeat
            if(!isPrime(p.longValue()) || !isPrime(q.longValue()) ||
                    p.longValue() == q.longValue())
                pqProduct = BigInteger.valueOf(0);
            else
                pqProduct = p.multiply(q);

            ++numAttempts;
            if(numAttempts > 1000000)
                throw new TimeoutException();

        } while (pqProduct.compareTo(lowerBound) < 1);
    }

    //
    // Builds an ArrayList of Strings that correspond to each line
    // of the resource file
    //
    private ArrayList<String> readPrimesFromFile(Scanner resourceFile)
    {
        ArrayList<String> primeNumbers = new ArrayList<String>();
        while (resourceFile.hasNextLine())
            primeNumbers.add(resourceFile.nextLine());

        return primeNumbers;
    }

    //
    // Calculate the value of phi to be used in the RSA algorithm
    //
    private void calculatePhi()
    {
        BigInteger pMinus1 =  p.subtract(BigInteger.valueOf(1));
        BigInteger qMinus1 = q.subtract(BigInteger.valueOf(1));
        phi = pMinus1.multiply(qMinus1);
    }

    //
    // Calculate the value of e to be used in the RSA algorithm
    //
    private void calculateE()
    {
        BigInteger gcd;

        e = BigInteger.valueOf(1); //Starting point for e

        //Keep trying values of e until one is found that is co-prime with phi
        do {
            e = e.add(BigInteger.valueOf(1));
            gcd = phi.gcd(e);
        } while(gcd.intValue() != 1);
    }

    //
    // Calculate the value of d to be used in the RSA algorithm
    //
    private void calculateD()
    {
        BigInteger result;

        BigInteger k = BigInteger.valueOf(-1); //Starting point for k

        //Find a value for k that evenly divides into (1 + k*phi)
        do {
            k = k.add(BigInteger.valueOf(1));
            result = k.multiply(phi);
            result = result.add(BigInteger.valueOf(1));
            result = result.mod(e);
        } while (result.intValue() != 0);

        //Calculate d based on k
        d = k.multiply(phi);
        d = d.add(BigInteger.valueOf(1));
        d = d.divide(e);
    }
}
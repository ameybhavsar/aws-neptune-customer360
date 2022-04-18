# aws-neptune-customer360
Web Application to show AWS Neptune Customer360 financial application use-cases/personas.

# Customer 360

    This demo servers 3 use cases: Customer Profile, Customer Card Recommendation, Bank Agent Portal
    Customer Profile: Customer logs into the financial portal to know about his financial landscape.
    Customer Card Recommendation: Customer calls credit agent for credit card recommendation. To get recommendation, Credit card agent : Based on recent purchases and bank offers makes a recommendation.
    Bank Agent Portal: Bank agent is looking customer to send credit card marketing emails.

## Customer 360 Resolution

Customer data comes in from many sources: 

    - retail purchases 
    - online purchases
    - loyalty programs
    - website visits 
    - mobile app installation

The data can come from multiple products, as a result of mergers and acquisitions and a thousand other reasons. 
Being able to understand customer preferences, behaviors, patterns relies on our ability to see the whole picture.
We can't see the picture if it's broken up into separate data stores that are hard to combine. 
By putting the customer data in a graph and stitching it together you can get to know your customers.

The dummy data in this demo has been masked. Depending on the sensitivity of your customer data and your industry it
may make sense for you to do the same. If your use case requires wild card searches then this option may not be appropriate.

## Financial Data

The data consists of nodes for:
    
    Bank, Customers, Card, Offer and Purchases along with relationships like HAS_ACCOUNT, HAS_CARD, HAS_OFFER, HAS_PURCHASED, IS_OFFERED.

In addition to the relationships between them.

## Graph Exploration

Being able to explore your highly connected data in real time in a graph visualization can help you "see" your business.
How does your data connect? What are the paths often traversed? How are the different segments of your customer base connected?
What does an abnormally connected node look like? Is it fraudulent, or synthetic? 
What are the details your business rules or models are not seeing that maybe an analyst can see?

## running

    mvn clean jooby:run

## building

    mvn clean package

## docker

     docker build . -t devcon
     docker run -p 80:80 -it devcon

## deploy

    docker build . -t devcon
    docker tag devcon:latest 805375321298.dkr.ecr.us-east-1.amazonaws.com/devcon:latest
    aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 805375321298.dkr.ecr.us-east-1.amazonaws.com
    docker push 805375321298.dkr.ecr.us-east-1.amazonaws.com/devcon:latest

## local connect

    ssh -i "demo-account-credentials.pem" -L 8182:devcon-demo.cluster-chnnbtwphuaq.us-east-1.neptune.amazonaws.com:8182 ubuntu@ec2-3-91-160-91.compute-1.amazonaws.com

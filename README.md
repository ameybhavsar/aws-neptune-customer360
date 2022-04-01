# aws-neptune-customer360
Web Application to show AWS Neptune Customer360

# Identity

    This demo servers 3 use cases: Identity Resolution, Householding, Graph Exploration

## Identity Resolution

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

The data consists of nodes for:
    
    Cities, Customers, Devices, Designated Market Areas, 
    Districts, Emails, Names, Payment Tokens, Phones, Stores and Zip Codes

In addition to the relationships between them.

In the "Results" tab can be seen a list of recently connected individuals by various shared Nodes like phones, devices, payment tokens.
Your data may include more or less connecting nodes like ip addresses, browser cookies from various vendors, geospatial information, etc.
You can create rules for what a valid match is, or assign weights to these relationships and only include those above a certain weight.
Your approach will depend on your data, and the "cost" of false positives or missed matches.

The matches can be automatically merged, or you have a grey area you can let analysts make a decision on which customers to merge together.
If you click on any of the customers, or the shared attributes a visualization of that node and its connections will appear.
You can then navigate the graph by clicking on any node to expand all of its neighbors.


## Householding

If your business has a "household component" then it is no longer enough to understand a single person but the household as a unit. 
Two married adults with the same last name is not the only way to define a household, recent demographic studies show many unmarried young adults living together.
Who is the customer? The individual or the household? Does a set of adults living together need more than one "Netflix" Subscription?
Do they need more than one "Blue Apron" or grocery delivery service? What about "Asurion Home" or other Home Electronics Insurance? 

Can you leverage word of mouth recommendations to convert the adults in a household to your products or services?
Can you reduce ad spend by marketing to the household instead of individual adults? 
Are you measuring marketing results by individual or household? Is that appropriate for your product or service?

You cannot rely on physical address alone for householding since you may not have captured that data.
Many products and services are digital and do not have a physical address tied to them.


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

     docker build . -t identity
     docker run -p 8080:8080 -it identity

## try:

    customer-d7db6e18613754041d1ae0916b6a3156
    customer-ceb1f8c1e24899ee8e4565212e44b63b
    customer-b077ed94773c89ad3b37769a23f9b6dd
    payment-token-2839190094816868
    device-PhEj580UA1VHO0TM
    name-Shakeel Swint

## deploy

    docker build . -t identity
    docker tag identity:latest 805375321298.dkr.ecr.us-east-1.amazonaws.com/identity:latest
    aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 805375321298.dkr.ecr.us-east-1.amazonaws.com
    docker push 805375321298.dkr.ecr.us-east-1.amazonaws.com/identity:latest

## local connect

    ssh -i "demo-account-credentials.pem" -L 8182:identity-demo.cluster-chnnbtwphuaq.us-east-1.neptune.amazonaws.com:8182 ubuntu@ec2-3-91-160-91.compute-1.amazonaws.com

## generate fake data

(customer:Customer {value:finance.iban} *100)
(customer)-[:HAS_DEVICE *n..1]->(device:Device {value: code.asin} *25)
(customer)-[:HAS_EMAIL *n..1]->(email:Email {value: internet.emailAddress} *25)
(customer)-[:HAS_NAME *n..1]->(name:Name {value: name.fullName} *25)
(customer)-[:HAS_TOKEN *n..1]->(token:PaymentToken {value: business.creditCardNumber} *25)
(customer)-[:HAS_PHONE *n..1]->(phone:Phone {value: phoneNumber.cellPhone} *25)

## generate found.csv

Run the application, then go to /match, wait for it finish, then sort the file by length of the lines in descending order :

    cat found.csv | perl -e 'print sort { length($b) <=> length($a) } <>' > found_sorted.csv

copy the results and overwrite found.csv (keep the header of found.csv (id)  and remove the last line from the founded_sorted.csv)

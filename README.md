# Recover DB password stored in my DBeaver connection

To recover/restore the Database passwords from DBeaver connection properties

More here:
https://stackoverflow.com/questions/39928401/recover-db-password-stored-in-my-dbeaver-connection/39928445#39928445


# How to use

clone or download repository:

git clone https://github.com/jaisonpjohn/dbeaver-password-retriever.git

# Compile
cd dbeaver-password-retriever
javac SimpleStringEncrypter.java

# Use
java SimpleStringEncrypter

# Example
~/dbeaver-password-retriever# java SimpleStringEncrypter

Please enter encrypted dbeaver password (file: .dbeaver-data-sources.xml): <enter password>

You have entered: "QlZVQKA ="

123

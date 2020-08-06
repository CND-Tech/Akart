<img src="https://img.shields.io/badge/Android_Studio-v4.0.1-blue"> 

# AKart Project  

Akart is an android application for student and admin for college store management. Students can add the items to purchase to cart. Admin can verify the student and purchases by scanning the unique QR code genenrated in the student app.<br><br>
<i>myapp</i> is the Student application and <i>Akart_Admin</i> is the admin android application

# Working
New users has to sign-up for new account. The users will be added to the users collection in google firebase 


After the user saves the add to cart list, the item details are stored in a collection named Items
```java
final DocumentReference documentRef = fstore.collection("Items").document(userID);
  add.setOnClickListener(new View.OnClickListener() {
  .
  .
  items.put(product_item, total);
  documentRef.set(items);
  .
  .
  }
```

The QR code is generated using the following code.
```java
UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();
qrgEncoder = new QRGEncoder(UserId, null, QRGContents.Type.TEXT,smallerdimension);
try{
            bitmap = qrgEncoder.encodeAsBitmap();
            qrcode.setImageBitmap(bitmap);
}
```
<br>

The Admin app scans the Qr-Code and extracts the UserID:
```java 
scan.setOnClickListener(new View.OnClickListener() {
@Override
public void onClick(View v) {
qrScan.initiateScan();
}
```
Once userID is got it searches the database for the document
```java
qrText = result.getContents().trim();
DocumentReference documentReference = firebaseFirestore.collection("Items").document(qrText);
DocumentReference doc = firebaseFirestore.collection("Users").document(qrText);
```

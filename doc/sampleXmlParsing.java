
    String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><root><something>a string to toast</something></root>";
        
    DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			StringReader reader = new StringReader(xml);
			InputSource source = new InputSource(reader);
			Document doc = documentBuilder.parse(source);
			Node node = doc.getElementsByTagName("something").item(0);
			Toast.makeText(this, node.getTextContent(), Toast.LENGTH_LONG ).show();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


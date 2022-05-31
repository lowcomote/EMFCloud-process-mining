package at.jku.se.eclipse.emf.ecore.listener;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

import org.deckfour.xes.extension.std.XConceptExtension;
import org.deckfour.xes.extension.std.XTimeExtension;
import org.deckfour.xes.factory.XFactory;
import org.deckfour.xes.factory.XFactoryBufferedImpl;
import org.deckfour.xes.model.XLog;
import org.deckfour.xes.model.XTrace;
import org.deckfour.xes.out.XesXmlSerializer;
import org.eclipse.emf.common.util.URI;
import at.jku.se.eclipse.emf.ecore.change.ChangeDescription;
import at.jku.se.eclipse.emf.ecore.change.util.ChangeRecorder;
import at.jku.se.ecorexes.ecoreXES.Log;

import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil.Copier;
import org.eclipse.emf.ecore.xmi.impl.XMIResourceFactoryImpl;

public class Listener {
	
	private static XFactory factory = new XFactoryBufferedImpl();
	private static XConceptExtension conceptExt = XConceptExtension.instance();
	private static XTimeExtension timeExt = XTimeExtension.instance();
	
	private XLog log;
	private XTrace trace;
	public ResourceSet resourceSet;
	private ChangeRecorder recorder;
	public URI uri;

	public Listener(ResourceSet resourceSet, URI uri) {
		this.uri = uri;
		log = getFactory().createLog();
		log.getExtensions().add(getConceptExt());
		log.getExtensions().add(getTimeExt());

		trace = getFactory().createTrace();
		recorder = new ChangeRecorder();
		recorder.beginRecording(Collections.singleton(resourceSet));
	}

	public void serializeLog() {
		log.add(trace);
		XesXmlSerializer serializer = new XesXmlSerializer();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH.mm");
		String formattedDateTime = LocalDateTime.now().format(formatter); // "2015-07-07 12.30"
		URI uri = this.uri.appendFileExtension(formattedDateTime);
		URI xesURI = uri.appendFileExtension("xes");
		URI changeURI = uri.appendFileExtension("change");
		URI ecorexesURI = uri.appendFileExtension("ecorexes");
		saveEcore(ecorexesURI);
		ChangeDescription ch = saveXMI(changeURI);
		log = recorder.getLog();
		System.out.println(xesURI.toFileString());
		try {
			File file = new File(xesURI.toFileString());
			file.getParentFile().mkdirs();
			file.createNewFile();
			OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
			// nested attributes : false
			serializer.serialize(log, out, false);
			out.flush();
			out.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public ChangeDescription saveXMI(URI uri) {
		ChangeDescription changeDescription = recorder.endRecording();
//		recorder.dispose();
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("change", new XMIResourceFactoryImpl());
		Resource resource = resourceSet.createResource(uri);
		Copier copier = new Copier();
		resource.getContents().add(copier.copy(changeDescription));
		copier.copyReferences();
		try {
			resource.save(null);
			System.out.println("saved to " + uri.toFileString());
		} catch (Exception e) {
			e.printStackTrace();
		}
		recorder.beginRecording(Collections.singleton(resourceSet));
		return changeDescription;
	}
	
	public void saveEcore(URI uri) {
		Log ecorelog = recorder.ecoreLog;
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("ecorexes", new XMIResourceFactoryImpl());
		Resource resource = resourceSet.createResource(uri);
		Copier copier = new Copier();
		resource.getContents().add(copier.copy(ecorelog));
		copier.copyReferences();
		try {
			resource.save(null);
			System.out.println("saved to " + uri.toFileString());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public static XFactory getFactory() {
		return factory;
	}

	public static XConceptExtension getConceptExt() {
		return conceptExt;
	}

	public static XTimeExtension getTimeExt() {
		return timeExt;
	}

}
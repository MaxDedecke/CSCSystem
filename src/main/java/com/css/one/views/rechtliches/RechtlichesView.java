package com.css.one.views.rechtliches;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Properties;

import com.css.one.data.AssociationRole;
import com.css.one.data.LawInfo;
import com.css.one.data.Person;
import com.css.one.data.Blossom;
import com.css.one.services.AssociationService;
import com.css.one.services.LawInfoService;
import com.css.one.services.PersonService;
import com.css.one.services.PromptingService;
import com.css.one.services.BlossomService;
import com.css.one.views.MainLayout;
import com.css.one.views.warenlager.WarenlagerView;
import com.vaadin.componentfactory.pdfviewer.PdfViewer;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.component.upload.receivers.FileBuffer;
import com.vaadin.flow.component.virtuallist.VirtualList;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.StreamResource;
import com.vaadin.flow.theme.lumo.LumoUtility;

import jakarta.annotation.security.PermitAll;

@PageTitle("Rechtliches")
@Route(value = "rechtliches", layout = MainLayout.class)
@PermitAll
public class RechtlichesView extends FlexLayout {

    private static final long serialVersionUID = -3563321934496915055L;

    private VerticalLayout statuteLayout;
    private VerticalLayout templateLayout;
    private VerticalLayout attorneyLayout;
    private VerticalLayout certificateLayout;
    private VerticalLayout memberPreventionLayout;
    private VerticalLayout trainingCertLayout;
    
    private Dialog showStatuteDialog = new Dialog();
    private Dialog uploadStatuteDialog = new Dialog();
    private Dialog attorneyInfoDialog = new Dialog();
    private Dialog showCertificateDialog = new Dialog();
    private Dialog uploadTrainingCertDialog = new Dialog();
    private PdfViewer statutePdfViewer = new PdfViewer();
    private PdfViewer certificatePdfViewer = new PdfViewer();

    private Text statuteName;
    private Text contactName = new Text("Ansprechpartner: -");
	private Text orgName = new Text("Kanzlei -");
	private Text contactPhone = new Text("Telefonnummer: -");
	private Text contactEmail = new Text("Email: -");

	private TextField nameField = new TextField("Ansprechpartner");
	private TextField phoneField = new TextField("Telefonnummer");
	private TextField emailField = new TextField("Email");
	private TextField orgNameField = new TextField("Kanzleiname");
	
    private BlossomService strainService;
    private PersonService personService;
    private AssociationService associationService;
    private LawInfoService lawInfoService;
    
    private int associationId;
    private LawInfo info;
    private Person trainingPerson;
    
    private String directoryPath;
	private InputStream streamStatute;
	private InputStream streamTrainingCert;
	private File pathToStatute;
	private File pathToTrainingCert;
	private String fileName;
    
	public RechtlichesView(BlossomService strainService, PersonService personService, AssociationService associationService, LawInfoService lawInfoService) {

		addClassName("law-view");
        
        this.strainService = strainService;
        this.personService = personService;
        this.associationService = associationService;
        this.lawInfoService = lawInfoService;
        		
        associationId = MainLayout.getAssociationId();
        
        setFlexDirection(FlexLayout.FlexDirection.COLUMN);
    	setHeightFull();
    	
        createStatuteComponent();
        createAttorneyComponent();
        createTemplatesComponent();
        createCertificatesComponent();
        createMemberPreventionComponent();
        createTrainingComponent();
        
        createShowStatuteDialog();
        createUploadStatuteDialog();
        createChangeAttorneyDialog();
        createShowCertificateDialog();
        createUploadTrainingCertDialog();
        
        HorizontalLayout layerOneLayout = new HorizontalLayout();
        layerOneLayout.setWidthFull();
        layerOneLayout.add(statuteLayout, attorneyLayout, templateLayout);
        layerOneLayout.setFlexGrow(1, statuteLayout, attorneyLayout, templateLayout);
        
        HorizontalLayout layerTwoLayout = new HorizontalLayout();
        layerTwoLayout.setWidthFull();
        layerTwoLayout.add(certificateLayout, memberPreventionLayout, trainingCertLayout);
        layerTwoLayout.setFlexGrow(1, certificateLayout, memberPreventionLayout, trainingCertLayout);
        
        add(layerOneLayout, layerTwoLayout);
        
        setFlexGrow(1, layerOneLayout);
        setFlexGrow(1, layerTwoLayout);
        
        getStyle().set("max-height", "100vh");
        
        Optional<LawInfo> optLawInfo = lawInfoService.getByAssociation(associationId);
		
		optLawInfo.ifPresentOrElse(a -> {
			info = a;
			statuteName.setText(a.getStatuteName());
			refreshAttorneyLayout(a);
		}, () -> refreshAttorneyLayout(null));
    }

	private void createUploadTrainingCertDialog() {		
		VerticalLayout mainLayout = new VerticalLayout();
		H3 h3 = new H3("Trainingszertifikat hochladen");
		
		Upload trainingCertUpload = new Upload();
		FileBuffer buffer = new FileBuffer();
		trainingCertUpload.setReceiver(buffer);
		trainingCertUpload.setAcceptedFileTypes(".pdf");
//		uploadCertificate.setMaxFileSize(16000);
		trainingCertUpload.setDropAllowed(true);
		trainingCertUpload.setMaxFiles(1);
		
		UploadI18N i18n = new UploadI18N();
        i18n.setDropFiles(new UploadI18N.DropFiles().setOne("PDF Datei hierhin ziehen...").setMany("PDF Dateien hierhin ziehen..."));
        i18n.setAddFiles(new UploadI18N.AddFiles().setOne("Zertifikat auswählen").setMany("Zertifikate auswählen"));
        i18n.setError(new UploadI18N.Error().setTooManyFiles("Zu viele Dateien.").setFileIsTooBig("Datei ist zu groß."));
        i18n.setUploading(new UploadI18N.Uploading().setStatus(new UploadI18N.Uploading.Status().setConnecting("Verbinden...").setStalled("Stillstand.").setProcessing("Verarbeiten der Datei..."))
                        .setRemainingTime(new UploadI18N.Uploading.RemainingTime().setPrefix("verbleibende Zeit: ").setUnknown("unbekannte verbleibende Zeit"))
                        .setError(new UploadI18N.Uploading.Error().setServerUnavailable("Server nicht verfügbar").setUnexpectedServerError("Unerwarteter Serverfehler").setForbidden("Verboten")));

        trainingCertUpload.setI18n(i18n);
        
        trainingCertUpload.addSucceededListener(event -> {
        	preparePathTrainingCert(trainingPerson);
            streamTrainingCert = buffer.getInputStream();
            pathToTrainingCert = new File(directoryPath, event.getFileName());
        });
		
		mainLayout.add(h3, trainingCertUpload);
				
		Button cancelButton = new Button("zurück");
		cancelButton.addClassName("cancel-button");
		cancelButton.addClickListener(e -> uploadStatuteDialog.close());
		
		Button uploadButton = new Button("upload");
		uploadButton.addClassName("save-button");
		uploadButton.addClickListener(e -> {
			
//			Optional<LawInfo> optLawInfo = lawInfoService.getByAssociation(associationId);
//			
//			optLawInfo.ifPresentOrElse(a -> {
//				
//				if(a.getStatutePath() != null) {
//					removeOldStatute(a);
//				}
//				
//				handleFile();
//				a.setStatuteName(fileName);
//				a.setStatutePath(pathToStatute.getAbsolutePath());
//				lawInfoService.update(a);
//				uploadStatuteDialog.close();
//				Notification.show("Neue Satzung erfolgreich hochgeladen.");
//				statuteName.setText(fileName);
//				updateStatutePdfComponent(a);
//			}, () -> {
//				LawInfo info = new LawInfo();
//				info.setAssociation(associationService.get(Integer.toUnsignedLong(associationId)).get());
//				info.setStatuteName(fileName);
//				info.setStatutePath(pathToStatute.getAbsolutePath());
//				lawInfoService.update(info);
//				uploadStatuteDialog.close();
//				statuteName.setText(fileName);
//				Notification.show("Neue Satzung erfolgreich hochgeladen.");
//				updateStatutePdfComponent(info);
//			});
//			
//			uploadStatuteDialog.close();
		});
		
		uploadTrainingCertDialog.add(mainLayout);
		uploadTrainingCertDialog.getFooter().add(cancelButton);
		uploadTrainingCertDialog.getFooter().add(uploadButton);
	}

	private void createTrainingComponent() {
		trainingCertLayout = new VerticalLayout();
		trainingCertLayout.addClassNames("rechtliches-box");
		
		HorizontalLayout typeLayout = new HorizontalLayout();
		typeLayout.add(new H3("Präventionsbeauftragter - Schulung"));
		
		Grid<Person> grid = new Grid<>();
		grid.addColumn(e -> e.getFirstName() + " " + e.getLastName()).setAutoWidth(true).setHeader("Name");
		
		HorizontalLayout gridLayout = new HorizontalLayout();
		gridLayout.setSizeFull();
		
		LitRenderer<Person> needsPreventionRenderer = LitRenderer.<Person>of(
				"<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
				.withProperty("icon", person -> !certificateIsMissing(person) ? "timer" : "check-circle")
				.withProperty("color", person -> !certificateIsMissing(person) ? "var(--lumo-error-color)"
						: "var(--lumo-success-color)");
		 
		grid.addColumn(needsPreventionRenderer).setAutoWidth(true).setHeader("Schulung");
		grid.addComponentColumn(item -> {
			if (certificateIsMissing(item)) {
				Button button = new Button("Zertifikat hochladen");
				button.addClassName("button-category-1");
				button.addClickListener(click -> {
					this.trainingPerson = item;
					uploadTrainingCertDialog.open();

				});
				button.addClassName("button-grid-green");

				return button;
			} else {
				return new Text("");
			}
        }).setAutoWidth(true);
		
		grid.setItems(personService.findAllByAssociation(associationId).stream().filter(e -> e.getAssociationRole() == AssociationRole.PREVENTION).toList());
		grid.setSizeFull();
		gridLayout.add(grid);
		gridLayout.setFlexGrow(1, grid);
		
		trainingCertLayout.add(typeLayout, gridLayout);
	}

	private boolean certificateIsMissing(Person item) {
		// TODO Auto-generated method stub
		return false;
	}

	private void createShowCertificateDialog() {
		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.add(certificatePdfViewer);
		mainLayout.setWidth(1000, Unit.PIXELS);
		
		Button cancelButton = new Button("zurück");
		cancelButton.addClassName("cancel-button");
		cancelButton.addClickListener(e -> showCertificateDialog.close());
		
		showCertificateDialog.add(mainLayout);
		showCertificateDialog.getFooter().add(cancelButton);		
	}

	private void createChangeAttorneyDialog() {
		FormLayout mainLayout = new FormLayout();

		mainLayout.add(orgNameField, nameField, phoneField, emailField);
		attorneyInfoDialog.add(mainLayout);
		
		Button buttonCancel = new Button("zurück");
		buttonCancel.addClassName("cancel-button");
		buttonCancel.addClickListener(e -> attorneyInfoDialog.close());
		
		Button buttonUpdate = new Button("update");
		buttonUpdate.addClassName("save-button");
		buttonUpdate.addClickListener(e -> {
			
			LawInfo tmpInfo;
			if(info != null) {
				tmpInfo = info;
			} else {
				tmpInfo = new LawInfo();
			}
			
			tmpInfo.setAttorneyEmail(emailField.getValue());
			tmpInfo.setAttorneyName(nameField.getValue());
			tmpInfo.setAttorneyPhone(phoneField.getValue());
			tmpInfo.setAttorneyOrgName(orgNameField.getValue());
			
			info = lawInfoService.update(tmpInfo);
			
			refreshAttorneyLayout(info);
			
			attorneyInfoDialog.close();
		});
		
		attorneyInfoDialog.getFooter().add(buttonCancel, buttonUpdate);
	}

	private void createUploadStatuteDialog() {
		VerticalLayout mainLayout = new VerticalLayout();
		H3 h3 = new H3("Satzung hochladen");
		
		Upload statuteUpload = new Upload();
		FileBuffer buffer = new FileBuffer();
		statuteUpload.setReceiver(buffer);
		statuteUpload.setAcceptedFileTypes(".pdf");
//		uploadCertificate.setMaxFileSize(16000);
		statuteUpload.setDropAllowed(true);
		statuteUpload.setMaxFiles(1);
		
		UploadI18N i18n = new UploadI18N();
        i18n.setDropFiles(new UploadI18N.DropFiles().setOne("PDF Datei hierhin ziehen...").setMany("PDF Dateien hierhin ziehen..."));
        i18n.setAddFiles(new UploadI18N.AddFiles().setOne("Satzung auswählen").setMany("Zertifikate auswählen"));
        i18n.setError(new UploadI18N.Error().setTooManyFiles("Zu viele Dateien.").setFileIsTooBig("Datei ist zu groß."));
        i18n.setUploading(new UploadI18N.Uploading().setStatus(new UploadI18N.Uploading.Status().setConnecting("Verbinden...").setStalled("Stillstand.").setProcessing("Verarbeiten der Datei..."))
                        .setRemainingTime(new UploadI18N.Uploading.RemainingTime().setPrefix("verbleibende Zeit: ").setUnknown("unbekannte verbleibende Zeit"))
                        .setError(new UploadI18N.Uploading.Error().setServerUnavailable("Server nicht verfügbar").setUnexpectedServerError("Unerwarteter Serverfehler").setForbidden("Verboten")));

        statuteUpload.setI18n(i18n);
        
        statuteUpload.addSucceededListener(event -> {
        	preparePath();
            streamStatute = buffer.getInputStream();
            pathToStatute = new File(directoryPath, event.getFileName());
            fileName = event.getFileName();
        });
		
		mainLayout.add(h3, statuteUpload);
				
		Button cancelButton = new Button("zurück");
		cancelButton.addClassName("cancel-button");
		cancelButton.addClickListener(e -> uploadStatuteDialog.close());
		
		Button uploadButton = new Button("upload");
		uploadButton.addClassName("save-button");
		uploadButton.addClickListener(e -> {
			
			Optional<LawInfo> optLawInfo = lawInfoService.getByAssociation(associationId);
			
			optLawInfo.ifPresentOrElse(a -> {
				
				if(a.getStatutePath() != null) {
					removeOldStatute(a);
				}
				
				handleFile();
				a.setStatuteName(fileName);
				a.setStatutePath(pathToStatute.getAbsolutePath());
				lawInfoService.update(a);
				uploadStatuteDialog.close();
				Notification.show("Neue Satzung erfolgreich hochgeladen.");
				statuteName.setText(fileName);
				updateStatutePdfComponent(a);
			}, () -> {
				LawInfo info = new LawInfo();
				info.setAssociation(associationService.get(Integer.toUnsignedLong(associationId)).get());
				info.setStatuteName(fileName);
				info.setStatutePath(pathToStatute.getAbsolutePath());
				lawInfoService.update(info);
				uploadStatuteDialog.close();
				statuteName.setText(fileName);
				Notification.show("Neue Satzung erfolgreich hochgeladen.");
				updateStatutePdfComponent(info);
			});
			
			uploadStatuteDialog.close();
		});
		
		uploadStatuteDialog.add(mainLayout);
		uploadStatuteDialog.getFooter().add(cancelButton);
		uploadStatuteDialog.getFooter().add(uploadButton);

	}

	private void removeOldStatute(LawInfo a) {
		File tmpFile = new File(a.getStatutePath());
		if(tmpFile.delete()) {
			a.setStatuteName(null);
			a.setStatutePath(null);
		}
	}

	private void createShowStatuteDialog() {
		VerticalLayout mainLayout = new VerticalLayout();
		        
		Optional<LawInfo> optional = lawInfoService.getByAssociation(associationId);
		optional.ifPresent(e -> {
			updateStatutePdfComponent(e);
			mainLayout.add(statutePdfViewer);
		});
		
		mainLayout.setWidth(1000, Unit.PIXELS);
		
		Button cancelButton = new Button("zurück");
		cancelButton.addClassName("cancel-button");
		cancelButton.addClickListener(e -> showStatuteDialog.close());
		
		showStatuteDialog.add(mainLayout);
		showStatuteDialog.getFooter().add(cancelButton);
	}

	private void updateStatutePdfComponent(LawInfo e) {
		if (e.getStatutePath() != null) {
			File file = new File(e.getStatutePath());
			if (file.exists()) {
				StreamResource resource = new StreamResource(e.getStatuteName(), () -> {
					try {
						return new FileInputStream(file);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
					}
					return streamStatute;
				});
				statutePdfViewer.setSrc(resource);
			}
		}
	}
	
	private void updateCertificatePdfComponent(Blossom e) {
		if (e.getPathOfCertificate() != null) {
			File file = new File(e.getPathOfCertificate());
			if (file.exists()) {
				StreamResource resource = new StreamResource("Zertifikat", () -> {
					try {
						return new FileInputStream(file);
					} catch (FileNotFoundException e1) {
						e1.printStackTrace();
						return streamStatute;
					}
				});
				certificatePdfViewer.setSrc(resource);
			}
		}
	}

	private void createMemberPreventionComponent() {
		memberPreventionLayout = new VerticalLayout();
		memberPreventionLayout.addClassNames("rechtliches-box");
		
		HorizontalLayout typeLayout = new HorizontalLayout();
		typeLayout.add(new H3("Prävention"));
		
		Grid<Person> grid = new Grid<>();
		grid.addColumn(e -> e.getFirstName() + " " + e.getLastName()).setAutoWidth(true).setHeader("Name");
		
		HorizontalLayout gridLayout = new HorizontalLayout();
		gridLayout.setSizeFull();
		
		LitRenderer<Person> needsPreventionRenderer = LitRenderer.<Person>of(
				"<vaadin-icon icon='vaadin:${item.icon}' style='width: var(--lumo-icon-size-s); height: var(--lumo-icon-size-s); color: ${item.color};'></vaadin-icon>")
				.withProperty("icon", important -> personIsSuspect(important) ? "eye" : "minus")
				.withProperty("color", important -> personIsSuspect(important) ? "var(--lumo-error-color)"
						: "var(--lumo-base-color)");
		 
		grid.addColumn(needsPreventionRenderer).setAutoWidth(true).setHeader("Verdacht").setComparator((sub1, sub2) -> Boolean.compare(personIsSuspect(sub1), personIsSuspect(sub2)));
		grid.setItems(personService.findAllByAssociation(associationId));
		grid.setSizeFull();
		gridLayout.add(grid);
		gridLayout.setFlexGrow(1, grid);
		memberPreventionLayout.add(typeLayout, gridLayout);
	}

	private boolean personIsSuspect(Person person) {
		
		PromptingService.getMemberPreventionPrompt();
		return false;
	}

	private void createCertificatesComponent() {
		certificateLayout = new VerticalLayout();
		certificateLayout.addClassNames("rechtliches-box");
		
		HorizontalLayout typeLayout = new HorizontalLayout();
		typeLayout.add(new H3("Laborzertifikate"));
		
		VerticalLayout layout = new VerticalLayout();
		layout.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Margin.Top.NONE);
		
		VirtualList<Blossom> list = new VirtualList<>();
		list.setRenderer(new ComponentRenderer<>(entry -> {
			CertificateEntryLayout entryLayout = new CertificateEntryLayout();
        	entryLayout.addClassName("diary-view-horizontal-layout-1");
        	entryLayout.setEntry(entry);
            return entryLayout;
        }));
		list.addClassNames(LumoUtility.Padding.NONE, LumoUtility.Margin.Top.NONE);
		list.setItems(strainService.findAllByAssociation(associationId));
		
		layout.add(list);
		certificateLayout.add(typeLayout, layout);
	}

	private void createAttorneyComponent() {
		attorneyLayout = new VerticalLayout();
		attorneyLayout.addClassNames("rechtliches-box");
		
		HorizontalLayout typeLayout = new HorizontalLayout();
		typeLayout.add(new H3("Partnerkanzlei"));
				
		VerticalLayout contactLayout = new VerticalLayout();
		contactLayout.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		HorizontalLayout space1 = new HorizontalLayout();
		space1.addClassNames(LumoUtility.Margin.MEDIUM, LumoUtility.Padding.NONE);
				
		VerticalLayout nameWrapper = new VerticalLayout();
		nameWrapper.add(contactName);
		nameWrapper.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		
		VerticalLayout phoneWrapper = new VerticalLayout();
		phoneWrapper.add(contactPhone);
		phoneWrapper.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		
		VerticalLayout emailWrapper = new VerticalLayout();
		emailWrapper.add(contactEmail);
		emailWrapper.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		
		contactLayout.add(orgName, nameWrapper, phoneWrapper, emailWrapper);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		Button buttonChangeAttorney = new Button("bearbeiten");
		buttonChangeAttorney.addClickListener(e -> {
			if(this.info != null) {
				nameField.setValue(info.getAttorneyName());
				phoneField.setValue(info.getAttorneyPhone());
				emailField.setValue(info.getAttorneyEmail());
				orgNameField.setValue(info.getAttorneyOrgName());
			}
			attorneyInfoDialog.open();
		});
		
		buttonChangeAttorney.addClassName("button-category-1");
		
		buttonLayout.add(buttonChangeAttorney);
		buttonLayout.setFlexGrow(1, buttonChangeAttorney);
		buttonLayout.setWidthFull();
		
		attorneyLayout.add(typeLayout, space1, contactLayout, buttonLayout);
	}
	
	private void refreshAttorneyLayout(LawInfo info) {
		if(info == null) {
		    contactName.setText("Ansprechpartner: -");
			orgName.setText("Kanzlei: -");
			contactPhone.setText("Telefonnummer: -");
			contactEmail.setText("Email: -");
		} else {
			if(info.getAttorneyName() != null) {
			    contactName.setText("Ansprechpartner: " + info.getAttorneyName());
			} else {				
				contactName.setText("Ansprechpartner: -");
			}
			
			if(info.getAttorneyOrgName() != null) {				
				orgName.setText("Kanzlei: " + info.getAttorneyOrgName());
			} else {
				orgName.setText("Kanzlei: -");
			}
			
			if(info.getAttorneyPhone() != null) {				
				contactPhone.setText("Telefonnummer: " + info.getAttorneyPhone());
			} else {
				contactPhone.setText("Telefonnummer: -");
			}
			
			if(info.getAttorneyEmail() != null) {				
				contactEmail.setText("Email: " + info.getAttorneyEmail());
			} else {
				contactEmail.setText("Email: -");
			}
		}
	}

	private void createTemplatesComponent() {
		templateLayout = new VerticalLayout();
		templateLayout.addClassNames("rechtliches-box");
		
		HorizontalLayout typeLayout = new HorizontalLayout();
		typeLayout.add(new H3("Vorlagen"));
		
		Text member = new Text("Mitglieder");
		HorizontalLayout memberLayout = new HorizontalLayout();
		Button buttonOpenSelfDisclosure = new Button("Selbstauskunft");
		buttonOpenSelfDisclosure.addClassName("button-category-1");
		Button buttonDeclaration = new Button("Verzichtserklärung");
		buttonDeclaration.addClassName("button-category-1");
		
		memberLayout.add(buttonOpenSelfDisclosure, buttonDeclaration);
		memberLayout.setWidthFull();
		memberLayout.setFlexGrow(1, buttonOpenSelfDisclosure, buttonDeclaration);
		
		Text club = new Text("Verein");
		HorizontalLayout clubLayout = new HorizontalLayout();
		Button buttonDataProtection = new Button("Datenschutzerklärung");
		buttonDataProtection.addClassName("button-category-1");
		clubLayout.add(buttonDataProtection);
		clubLayout.setWidthFull();
		clubLayout.setFlexGrow(1, buttonDataProtection);
		
		templateLayout.add(typeLayout, member, memberLayout, new Hr(), club, clubLayout, new Hr());
	}

	private void createStatuteComponent() {
		statuteLayout = new VerticalLayout();
		statuteLayout.addClassNames("rechtliches-box");
		
		HorizontalLayout typeLayout = new HorizontalLayout();
		typeLayout.add(new H3("Satzung"));

		HorizontalLayout imageLayout = new HorizontalLayout();
		StreamResource imageResource = new StreamResource("CLOS.png",
				() -> getClass().getResourceAsStream("/CLOS.png"));

		Image logoImage = new Image(imageResource, "");
		logoImage.setHeight(250, Unit.PIXELS);
		imageLayout.add(logoImage);
		imageLayout.setWidthFull();
		imageLayout.setJustifyContentMode(JustifyContentMode.CENTER);
		imageLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		imageLayout.addClassName(LumoUtility.Margin.Bottom.MEDIUM);
		
		HorizontalLayout nameLayout = new HorizontalLayout();
		statuteName = new Text("beispielSatzung.pdf");
		nameLayout.add(statuteName);
		nameLayout.setWidthFull();
		nameLayout.setJustifyContentMode(JustifyContentMode.CENTER);
		nameLayout.setDefaultVerticalComponentAlignment(Alignment.CENTER);
		nameLayout.addClassName(LumoUtility.Margin.Bottom.MEDIUM);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		
		Button buttonOpenStatute = new Button("anzeigen");
		buttonOpenStatute.addClassName("button-category-1");
		buttonOpenStatute.addClickListener(e -> showStatuteDialog.open());
		
		Button buttonUploadNewStatute = new Button("hochladen");
		buttonUploadNewStatute.addClassName("button-category-1");
		buttonUploadNewStatute.addClickListener(e -> uploadStatuteDialog.open());
		
		buttonLayout.add(buttonOpenStatute, buttonUploadNewStatute);
		buttonLayout.setFlexGrow(1, buttonOpenStatute, buttonUploadNewStatute);
		buttonLayout.setWidthFull();
		
		statuteLayout.add(typeLayout, imageLayout, nameLayout, buttonLayout);

	}

	private void handleFile() {

	    File targetFile = pathToStatute;
	    try {
	        if (!targetFile.exists()) {
	            targetFile.createNewFile();
	        }
	        try (FileOutputStream out = new FileOutputStream(targetFile)) {
	        	// 16 KB buffer
	            byte[] buffer = new byte[16384];
	            int bytesRead;
	            while ((bytesRead = streamStatute.read(buffer)) != -1) {
	                out.write(buffer, 0, bytesRead);
	            }
	        }
	    } catch (IOException e) {
	        e.printStackTrace();
	        Notification.show("Fehler beim Speichern der Datei");
	    }
	    
	    streamStatute = null;
	}
	
	private void preparePath() {
		final Properties properties = new Properties();
		try (InputStream input = new FileInputStream(new File("/application.properties"))) {

			// Load the properties file
			properties.load(input);
		} catch (IOException ex) {
			try (InputStream input = WarenlagerView.class.getClassLoader().getResourceAsStream("application.properties")) {
				if (input == null) {
					System.out.println("Sorry, unable to find application.properties");
					System.exit(1);
				}

				// Load the properties file
				properties.load(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		directoryPath = properties.getProperty("certificate.upload.path") + File.separator +  associationId + File.separator + "statute" + File.separator;
	    Path path = Paths.get(directoryPath);

	    // Überprüfe, ob das Verzeichnis existiert
	    if (!Files.exists(path)) {
	        try {
	            // Erstelle das Verzeichnis, falls es nicht existiert
	            Files.createDirectories(path);
	        } catch (IOException e) {
	            e.printStackTrace();
	            Notification.show("Fehler beim Erstellen des Verzeichnisses");
	            return; // Beende die Methode, falls das Verzeichnis nicht erstellt werden kann
	        }
	    }
	}
	
	private void preparePathTrainingCert(Person p) {
		final Properties properties = new Properties();
		try (InputStream input = new FileInputStream(new File("/application.properties"))) {

			// Load the properties file
			properties.load(input);
		} catch (IOException ex) {
			try (InputStream input = WarenlagerView.class.getClassLoader().getResourceAsStream("application.properties")) {
				if (input == null) {
					System.out.println("Sorry, unable to find application.properties");
					System.exit(1);
				}

				// Load the properties file
				properties.load(input);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		directoryPath = properties.getProperty("certificate.upload.path") + File.separator +  associationId + File.separator + "prevention" + File.separator + "training" + File.separator + p.getId();
	    Path path = Paths.get(directoryPath);

	    // Überprüfe, ob das Verzeichnis existiert
	    if (!Files.exists(path)) {
	        try {
	            // Erstelle das Verzeichnis, falls es nicht existiert
	            Files.createDirectories(path);
	        } catch (IOException e) {
	            e.printStackTrace();
	            Notification.show("Fehler beim Erstellen des Verzeichnisses");
	            return; // Beende die Methode, falls das Verzeichnis nicht erstellt werden kann
	        }
	    }
	}
	
	public class CertificateEntryLayout extends HorizontalLayout {


	    private static final long serialVersionUID = 8051256955571934106L;
		private Text name;
	    private Avatar avatar;
	    private Button buttonOpenCertificate;
	    private Blossom entry;
	    
	    public CertificateEntryLayout() {
	    	
	    	setWidthFull();
	    	addClassNames(LumoUtility.Padding.Top.MEDIUM, LumoUtility.Padding.Left.MEDIUM, LumoUtility.Padding.Right.MEDIUM,
	    			LumoUtility.Margin.Top.XSMALL);
	    	
	    	VerticalLayout nameWrapper = new VerticalLayout();
	    	name = new Text("Test");
	    	nameWrapper.add(name);
	    	
	    	VerticalLayout buttonWrapper = new VerticalLayout();
	    	buttonOpenCertificate = new Button("Zertifikat ansehen");
	    	buttonOpenCertificate.addClassName("button-category-1");
	    	buttonOpenCertificate.addClickListener(e -> {
	    		updateCertificatePdfComponent(entry);
	    		showCertificateDialog.open();
	    	});
	    	buttonWrapper.add(buttonOpenCertificate);
	    	buttonWrapper.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.Top.SMALL);
	    	
	    	VerticalLayout avatarWrapper = new VerticalLayout();
	    	StreamResource imageResource = new StreamResource("seed.png",
	    			() -> getClass().getResourceAsStream("/seed.png"));

	    	avatar = new Avatar("");
			avatar.setImageResource(imageResource);
            avatar.setHeight("32px");
            avatar.setWidth("32px");
            avatarWrapper.add(avatar);
            avatarWrapper.addClassNames(LumoUtility.Margin.Bottom.MEDIUM);
	        add(avatarWrapper, nameWrapper, buttonWrapper);
	    }

		public void setEntry(Blossom entry) {
			this.entry = entry;
			name.setText(entry.getName());
		}

		public String renderDate(LocalDate date) {
	    	String day = "";
			String month = "";

			if (date != null) {

				if (date.getDayOfMonth() < 10) {
					day = "0" + String.valueOf(date.getDayOfMonth());
				} else {
					day = String.valueOf(date.getDayOfMonth());
				}

				if (date.getMonthValue() < 10) {
					month = "0" + String.valueOf(date.getMonthValue());
				} else {
					month = String.valueOf(date.getMonthValue());
				}

				return day + "." + month + "." + date.getYear();
			} else {
				return "-";
			}
	    }
	}
}

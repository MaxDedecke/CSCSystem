package com.css.one.views.rechtliches;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.Optional;
import java.util.Properties;

import com.css.one.data.Association;
import com.css.one.data.Person;
import com.css.one.data.Strain;
import com.css.one.services.AssociationService;
import com.css.one.services.PersonService;
import com.css.one.services.PromptingService;
import com.css.one.services.StrainService;
import com.css.one.views.MainLayout;
import com.css.one.views.warenlager.WarenlagerView;
import com.vaadin.componentfactory.pdfviewer.PdfViewer;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Hr;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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
    
    private Dialog showStatuteDialog = new Dialog();
    private Dialog uploadStatuteDialog = new Dialog();
    private Dialog attorneyInfoDialog = new Dialog();
    private Dialog showCertificateDialog = new Dialog();
    
    private StrainService strainService;
    private PersonService personService;
    private AssociationService associationService;
    
    private int associationId;
    
    private String directoryPath;
	private InputStream streamStatute;
	private File pathToStatute;
    
	public RechtlichesView(StrainService strainService, PersonService personService, AssociationService associationService) {
        
		addClassName("law-view");
        
        this.strainService = strainService;
        this.personService = personService;
        this.associationService = associationService;
        
        associationId = MainLayout.getAssociationId();
        
        setFlexDirection(FlexLayout.FlexDirection.COLUMN);
    	setHeightFull();
    	
        createStatuteComponent();
        createAttorneyComponent();
        createTemplatesComponent();
        createCertificatesComponent();
        createMemberPreventionComponent();
        
        createShowStatuteDialog();
        createUploadStatuteDialog();
        
        HorizontalLayout layerOneLayout = new HorizontalLayout();
        layerOneLayout.setWidthFull();
        layerOneLayout.add(statuteLayout, attorneyLayout, templateLayout);
        layerOneLayout.setFlexGrow(1, statuteLayout, attorneyLayout, templateLayout);
        
        HorizontalLayout layerTwoLayout = new HorizontalLayout();
        layerTwoLayout.setWidthFull();
        layerTwoLayout.add(certificateLayout, memberPreventionLayout);
        layerTwoLayout.setFlexGrow(1, certificateLayout, memberPreventionLayout);
        
        add(layerOneLayout, layerTwoLayout);
        
        setFlexGrow(1, layerOneLayout);
        setFlexGrow(1, layerTwoLayout);
        
        getStyle().set("max-height", "100vh");
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
        i18n.setDropFiles(new UploadI18N.DropFiles().setOne("Datei hierhin ziehen...").setMany("Dateien hierhin ziehen..."));
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
            
        });
		
		mainLayout.add(h3, statuteUpload);
				
		Button cancelButton = new Button("zurück");
		cancelButton.addClassName("cancel-button");
		cancelButton.addClickListener(e -> uploadStatuteDialog.close());
		
		Button uploadButton = new Button("upload");
		uploadButton.addClassName("save-button");
		uploadButton.addClickListener(e -> {
			Optional<Association> optAssociation = associationService.get(Integer.toUnsignedLong(associationId));
			optAssociation.ifPresent(a -> {
				handleFile();
				a.setStatutePath(pathToStatute.getAbsolutePath());
				associationService.update(a);
				uploadStatuteDialog.close();
				Notification.show("Neue Satzung erfolgreich hochgeladen.");
			});
		});
		
		uploadStatuteDialog.add(mainLayout);
		uploadStatuteDialog.getFooter().add(cancelButton);
		uploadStatuteDialog.getFooter().add(uploadButton);

	}

	private void createShowStatuteDialog() {
		VerticalLayout mainLayout = new VerticalLayout();

        PdfViewer pdfViewer = new PdfViewer();
        
		Optional<Association> optional = associationService.get(Integer.toUnsignedLong(associationId));
		optional.ifPresentOrElse(e -> {
            StreamResource resource = new StreamResource("example.pdf", () -> getClass().getResourceAsStream(e.getStatutePath()));
			pdfViewer.setSrc(resource);
			mainLayout.add(pdfViewer);
		}, () -> {
			
		});
		
		showStatuteDialog.add(mainLayout);
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
		
		VirtualList<Strain> list = new VirtualList<>();
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
		
		Text contactName = new Text("Ansprechpartner: Herr Anwalt");
		
		VerticalLayout phoneWrapper = new VerticalLayout();
		Text contactPhone = new Text("Telefonnummer: 0941/23345443");
		phoneWrapper.add(contactPhone);
		phoneWrapper.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		
		VerticalLayout emailWrapper = new VerticalLayout();
		Text contactEmail = new Text("Email: kanzleideinerwahl@gmail.com");
		emailWrapper.add(contactEmail);
		emailWrapper.addClassNames(LumoUtility.Margin.NONE, LumoUtility.Padding.NONE);
		
		contactLayout.add(contactName, phoneWrapper, emailWrapper);
		
		HorizontalLayout buttonLayout = new HorizontalLayout();
		Button buttonChangeAttorney = new Button("bearbeiten");
		buttonChangeAttorney.addClickListener(e -> attorneyInfoDialog.open());
		
		buttonChangeAttorney.addClassName("button-category-1");
		
		buttonLayout.add(buttonChangeAttorney);
		buttonLayout.setFlexGrow(1, buttonChangeAttorney);
		buttonLayout.setWidthFull();
		
		attorneyLayout.add(typeLayout, space1, contactLayout, buttonLayout);
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
		Text statuteName = new Text("beispielSatzung.pdf");
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
		
		Button buttonDownloadStatue = new Button("herunterladen");
		buttonDownloadStatue.addClassName("button-category-1");
		
		buttonLayout.add(buttonOpenStatute, buttonUploadNewStatute, buttonDownloadStatue);
		buttonLayout.setFlexGrow(1, buttonOpenStatute, buttonUploadNewStatute, buttonDownloadStatue);
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
	
	public class CertificateEntryLayout extends HorizontalLayout {


	    private static final long serialVersionUID = 8051256955571934106L;
		private Text name;
		private Text dateOfTest;
	    private Avatar avatar;
	    private VerticalLayout innerLayout;
	    private Button buttonOpenCertificate;
	    
	    public CertificateEntryLayout() {
	    	
	    	name = new Text("Test");
	    	dateOfTest = new Text("Test");
	    	buttonOpenCertificate = new Button("Zertifikat ansehen");
	    	buttonOpenCertificate.addClassName("button-category-1");
	    	buttonOpenCertificate.addClickListener(e -> showCertificateDialog.open());
	    	
			avatar = new Avatar("");
			StreamResource imageResource = new StreamResource("seed.png",
					() -> getClass().getResourceAsStream("/seed.png"));

			avatar.setImageResource(imageResource);
            avatar.setHeight("32px");
            avatar.setWidth("32px");
            avatar.getElement().setAttribute("tabindex", "-1");
            avatar.addClassName(LumoUtility.Margin.Top.MEDIUM);
	        setWidthFull();
	        addClassNames(LumoUtility.Padding.Top.MEDIUM, LumoUtility.Padding.Left.MEDIUM, LumoUtility.Padding.Right.MEDIUM,
	        		LumoUtility.Margin.Top.XSMALL);
	        
	        innerLayout = new VerticalLayout();
	        innerLayout.addClassName("diary-view-vertical-layout-1");
	        innerLayout.setMinHeight(100, Unit.PIXELS);
	        
	        HorizontalLayout dateWrapper = new HorizontalLayout();
	        dateWrapper.setWidthFull();
	        dateWrapper.add(name, dateOfTest, buttonOpenCertificate);
	        dateWrapper.addClassName("right-to-left-layout");
	        
	        innerLayout.add(dateWrapper);
	        
	        VerticalLayout avatarLayout = new VerticalLayout();
	        avatarLayout.addClassName("diary-view-vertical-layout-2");
	        avatarLayout.add(avatar);
	        add(avatarLayout, innerLayout);
	    }

	    public void setEntry(Strain entry) {
	    	
	       name.setText(entry.getName());
	       dateOfTest.setText(renderDate(entry.getDatePlanted()));
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

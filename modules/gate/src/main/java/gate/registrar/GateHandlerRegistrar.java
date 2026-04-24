package gate.registrar;

import gate.command.HTMLCommand;
import gate.command.HideCommand;
import gate.command.RedirectCommand;
import gate.command.ReloadCommand;
import gate.handler.*;
import gate.lang.json.*;
import gate.report.Report;
import gate.type.*;
import gate.type.mime.MimeData;
import gate.type.mime.MimeDataFile;
import gate.type.mime.MimeText;
import gate.type.mime.MimeTextFile;
import jakarta.ws.rs.core.Response;

import java.io.File;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;

public class GateHandlerRegistrar implements HandlerRegistrar
{

	@Override
	public void register(Map<Class<?>, Class<? extends Handler>> registry)
	{
		registry.put(byte[].class, ByteArrayHandler.class);
		registry.put(String[][].class, StringMatrixHandler.class);
		registry.put(String.class, StringHandler.class);
		registry.put(File.class, FileHandler.class);
		registry.put(Integer.class, IntegerHandler.class);
		registry.put(Character.class, CharacterHandler.class);
		registry.put(LocalDateTime.class, ConverterHandler.class);
		registry.put(Enum.class, EnumHandler.class);
		registry.put(Path.class, PathHandler.class);
		registry.put(Response.class, JAXRSResponseHandler.class);
		registry.put(HideCommand.class, HideCommandHandler.class);
		registry.put(HTMLCommand.class, HTMLCommandHandler.class);
		registry.put(RedirectCommand.class, RedirectCommandHandler.class);
		registry.put(ReloadCommand.class, ReloadCommandHandler.class);
		registry.put(TempFile.class, TempFileHandler.class);
		registry.put(SafeStyle.class, SafeStyleHandler.class);
		registry.put(MimeDataFile.class, MimeDataFileHandler.class);
		registry.put(MimeTextFile.class, MimeTextFileHandler.class);
		registry.put(MimeText.class, MimeTextHandler.class);
		registry.put(MimeData.class, MimeDataHandler.class);
		registry.put(Form.class, FormHandler.class);
		registry.put(SafeHTML.class, SafeHTMLHandler.class);
		registry.put(ID.class, IDHandler.class);
		registry.put(HttpError.class, HttpErrorHandler.class);
		registry.put(SafeText.class, SafeTextHandler.class);
		registry.put(SafeName.class, SafeNameHandler.class);
		registry.put(Result.class, ResultHandler.class);
		registry.put(Version.class, VersionHandler.class);
		registry.put(DataFile.class, DataFileHandler.class);
		registry.put(PNG.class, PNGHandler.class);
		registry.put(NamedTempFile.class, NamedTempFileHandler.class);
		registry.put(JsonElement.class, JsonElementHandler.class);
		registry.put(JsonArray.class, JsonElementHandler.class);
		registry.put(JsonBoolean.class, JsonElementHandler.class);
		registry.put(JsonNull.class, JsonElementHandler.class);
		registry.put(JsonNumber.class, JsonElementHandler.class);
		registry.put(JsonObject.class, JsonElementHandler.class);
		registry.put(JsonScalar.class, JsonElementHandler.class);
		registry.put(JsonString.class, JsonElementHandler.class);
		registry.put(Report.class, ReportHandler.class);
	}
}
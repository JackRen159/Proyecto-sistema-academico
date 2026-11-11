export * from './acudientes.service';
import { AcudientesService } from './acudientes.service';
export * from './autenticacin.service';
import { AutenticacinService } from './autenticacin.service';
export * from './estudiantes.service';
import { EstudiantesService } from './estudiantes.service';
export * from './vinculaciones.service';
import { VinculacionesService } from './vinculaciones.service';
export const APIS = [AcudientesService, AutenticacinService, EstudiantesService, VinculacionesService];
